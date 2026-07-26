package com.rafay.locationService.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.locationService.DTO.LocationRequestDTO;
import com.rafay.locationService.DTO.NearbySearchResultDto;
import com.rafay.locationService.Service.locationService.NearbySearchService;

@ExtendWith(MockitoExtension.class)
public class LocationControllerTest {

    @Mock
    private NearbySearchService nearbySearchService;

    @InjectMocks
    private LocationController locationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private LocationRequestDTO request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(locationController).build();
        objectMapper = new ObjectMapper();

        request = new LocationRequestDTO();
        request.setUserId("user1");
        request.setLatitude(new BigDecimal("37.7749"));
        request.setLongitude(new BigDecimal("-122.4194"));
    }

    @Test
    void testGetRecommendations() throws Exception {
        when(nearbySearchService.processNearbySearchAsList(any(LocationRequestDTO.class)))
                .thenReturn(List.of("user2", "user3"));

        mockMvc.perform(post("/location/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("user2"))
                .andExpect(jsonPath("$[1]").value("user3"));

        verify(nearbySearchService, times(1)).processNearbySearchAsList(any(LocationRequestDTO.class));
    }

    @Test
    void testGetNearbySearchSync() throws Exception {
        NearbySearchResultDto responseDto = new NearbySearchResultDto("user1", List.of("user2", "user3"));

        when(nearbySearchService.processNearbySearch(any(LocationRequestDTO.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/location/nearby-search/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("user1"))
                .andExpect(jsonPath("$.nearby_user_ids[0]").value("user2"))
                .andExpect(jsonPath("$.nearby_user_ids[1]").value("user3"));

        verify(nearbySearchService, times(1)).processNearbySearch(any(LocationRequestDTO.class));
    }
}