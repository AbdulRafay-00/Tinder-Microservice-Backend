package com.rafay.locationService.Service.locationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.locationService.DTO.LocationRequestDTO;
import com.rafay.locationService.DTO.NearbySearchResultDto;

@ExtendWith(MockitoExtension.class)
public class NearbySearchServiceTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private NearbySearchService nearbySearchService;

    private String userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocationRequestDTO request;

    @BeforeEach
    void setUp() {
        userId = "user1";
        latitude = new BigDecimal("37.7749");
        longitude = new BigDecimal("-122.4194");

        request = new LocationRequestDTO();
        request.setUserId(userId);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
    }

    @Test
    void testProcessNearbySearch() {
        when(locationService.getRecommendations(userId, latitude, longitude))
                .thenReturn(List.of("user2", "user3"));

        NearbySearchResultDto result = nearbySearchService.processNearbySearch(userId, latitude, longitude);

        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getNearbyUserIds().size());

        verify(locationService, times(1)).getRecommendations(userId, latitude, longitude);
    }

    @Test
    void testProcessNearbySearch_WithDto() {
        when(locationService.getRecommendations(userId, latitude, longitude))
                .thenReturn(List.of("user2", "user3"));

        NearbySearchResultDto result = nearbySearchService.processNearbySearch(request);

        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getNearbyUserIds().size());
        assertEquals(List.of("user2", "user3"), result.getNearbyUserIds());

        verify(locationService, times(1)).getRecommendations(userId, latitude, longitude);
    }

    @Test
    void testProcessNearbySearchAsList() {
        when(locationService.getRecommendations(userId, latitude, longitude))
                .thenReturn(List.of("user2", "user3"));

        List<String> result = nearbySearchService.processNearbySearchAsList(request);

        assertEquals(List.of("user2", "user3"), result);

        verify(locationService, times(1)).getRecommendations(userId, latitude, longitude);
    }
}