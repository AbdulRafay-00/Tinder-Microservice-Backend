package com.rafay.Orchestration_Service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.Orchestration_Service.DTO.LocationRequset;
import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;
import com.rafay.Orchestration_Service.GlobalExeption.NoNearbyUsersException;
import com.rafay.Orchestration_Service.service.NearUserList;

@ExtendWith(MockitoExtension.class)
class UserReqTest {

    @Mock
    private NearUserList nearUserList;

    @InjectMocks
    private UserReq userReq;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String userId;
    private LocationRequset locationRequest;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userReq).build();
        objectMapper = new ObjectMapper();

        userId = "user123";
        locationRequest = new LocationRequset();
        locationRequest.setLatitude(new BigDecimal("40.7128"));
        locationRequest.setLongitude(new BigDecimal("-74.0060"));
    }

    @Test
    @DisplayName("POST /discovery returns 200 with nearby users on success")
    void testGetDiscovery_success() throws Exception {
        NearbySearchResultDto responseDto =
                new NearbySearchResultDto(userId, Arrays.asList("user456", "user789"));

        when(nearUserList.getNearUserList(any(LocationRequset.class), eq(userId)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/discovery")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId))
                .andExpect(jsonPath("$.nearby_user_ids.length()").value(2))
                .andExpect(jsonPath("$.nearby_user_ids[0]").value("user456"))
                .andExpect(jsonPath("$.nearby_user_ids[1]").value("user789"));

        verify(nearUserList).getNearUserList(any(LocationRequset.class), eq(userId));
    }

    @Test
    @DisplayName("POST /discovery propagates exception when no nearby users found")
    void testGetDiscovery_noNearbyUsers_throws() throws Exception {
        when(nearUserList.getNearUserList(any(LocationRequset.class), eq(userId)))
                .thenThrow(new NoNearbyUsersException(userId));

        // No @ControllerAdvice confirmed yet for this service, so the exception
        // surfaces raw through MockMvc rather than becoming a handled HTTP response.
        // If you add a global exception handler later, replace this with a direct
        // status()/jsonPath() assertion instead.
        try {
            mockMvc.perform(post("/discovery")
                    .header("X-User-Id", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(locationRequest)));
            fail("Expected NoNearbyUsersException to propagate");
        } catch (Exception e) {
            Throwable rootCause = e.getCause() != null ? e.getCause() : e;
            assertTrue(rootCause instanceof NoNearbyUsersException);
        }
    }

    @Test
    @DisplayName("POST /discovery missing X-User-Id header returns 400")
    void testGetDiscovery_missingUserIdHeader_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/discovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isBadRequest());

        verify(nearUserList, never()).getNearUserList(any(), anyString());
    }
}