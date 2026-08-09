package com.rafay.Orchestration_Service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.rafay.Orchestration_Service.testcontainerconfig.BaseIntegrationTest;

public class UserReqIT extends BaseIntegrationTest {
    @Autowired
    MockMvc mockMvc;
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
}
