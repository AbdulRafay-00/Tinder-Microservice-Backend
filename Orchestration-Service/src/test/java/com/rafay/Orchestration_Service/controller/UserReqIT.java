package com.rafay.Orchestration_Service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rafay.Orchestration_Service.DTO.NearLocationRequestDTO;
import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.FilteredList;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.NearByReq;
import com.rafay.Orchestration_Service.testcontainerconfig.BaseIntegrationTest;

public class UserReqIT extends BaseIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    NearByReq nearByReq;

    @MockitoBean
    FilteredList filteredList;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Test
    public void redisCacheHitS()throws Exception {
        String userId = "Testuser123";


    redisTemplate.opsForList().rightPushAll(
        "discovery:" + userId,
        List.of("user1", "user2", "user3")
    );
        String requestBody = """
        { "latitude": 40.7128,
        "longitude": -74.0060
        }
                """;
                
                
        

        mockMvc.perform(post("/discovery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody)
        .header("X-User-Id", userId)).andExpect(status().isOk());
    }


    @Test
    void redisCacheMiss() throws Exception {

        String userId = "user123";

        // Location Service response
        NearbySearchResultDto locationResult =
                new NearbySearchResultDto(
                        userId,
                        List.of("user1", "user2", "user3")
                );

        when(nearByReq.getNearbySearchSync(any(NearLocationRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(locationResult));

        // Match Service response after filtering
        NearbySearchResultDto filteredResult =
                new NearbySearchResultDto(
                        userId,
                        List.of("user1", "user3")
                );

        when(filteredList.acceptNearbyUsers(any(NearbySearchResultDto.class)))
                .thenReturn(ResponseEntity.ok(filteredResult));

        String requestBody = """
                {
                    "latitude": 40.7128,
                    "longitude": -74.0060
                }
                """;

        mockMvc.perform(
                post("/discovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-User-Id", userId)
        )
        .andExpect(status().isOk());
    }
}
