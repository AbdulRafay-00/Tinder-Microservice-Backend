package com.rafay.Orchestration_Service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.AccessFlag.Location;
import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.rafay.Orchestration_Service.DTO.LocationRequset;
import com.rafay.Orchestration_Service.DTO.NearLocationRequestDTO;
import com.rafay.Orchestration_Service.DTO.NearbySearchResultDto;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.FilteredList;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.NearByReq;
import com.rafay.Orchestration_Service.GlobalExeption.NoNearbyUsersException;
import com.rafay.Orchestration_Service.Redis.NearByUserCache;

public class NearUserListTest {
    @Mock
    private NearByReq nearByReq;
    @Mock
    private FilteredList filteredList;
    @Mock
    private NearByUserCache nearByUserCache;

    @InjectMocks
    private NearUserList nearUserList;

 // shared across tests, declared as fields
    private LocationRequset locationRequest;
    private String userId;

    @BeforeEach
    void setup() {
        locationRequest = new LocationRequset();
        locationRequest.setLatitude(new BigDecimal("40.7128"));
        locationRequest.setLongitude(new BigDecimal("-74.0060"));
        userId = "user123";
    }
    @Test
    @DisplayName("Test getNearUserList method when cache has nearby users")
    @ExtendWith(MockitoExtension.class)
    void testGetNearUserList() {
        when(nearByUserCache.getCachedNearbyUsers(userId)).thenReturn(Arrays.asList("user456", "user789"));
        NearbySearchResultDto result = nearUserList.getNearUserList(locationRequest, userId);

        // Assert
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getNearbyUserIds().size());
        assertTrue(result.getNearbyUserIds().contains("user456"));
        assertTrue(result.getNearbyUserIds().contains("user789"));
        verify(nearByReq, never()).getNearbySearchSync(any());
        verify(filteredList, never()).acceptNearbyUsers(any());
        verify(nearByUserCache, never()).cacheNearbyUsers(anyString(), anyList());
    }



    @Test
    @DisplayName("Test getNearUserList method when no nearby users are found")
    @ExtendWith(MockitoExtension.class)
    void testGetNearUserList_CacheMiss_NoNearbyUsers() {
        NearLocationRequestDTO nearLocationRequestDTO = new NearLocationRequestDTO();
        nearLocationRequestDTO.setUserId(userId);
        nearLocationRequestDTO.setLatitude(locationRequest.getLatitude());
        nearLocationRequestDTO.setLongitude(locationRequest.getLongitude());

        // Arrange
        when(nearByUserCache.getCachedNearbyUsers(userId))
            .thenReturn(null);
        
        when(nearByReq.getNearbySearchSync(nearLocationRequestDTO))
            .thenReturn(null);
        
        NoNearbyUsersException exception = assertThrows(NoNearbyUsersException.class, () -> {
            nearUserList.getNearUserList(locationRequest, userId);
        });

        assertTrue(exception.getMessage().contains(userId));

    }

    @Test
    @DisplayName("Test getNearUserList method when cache miss and filtered list is empty")
    @ExtendWith(MockitoExtension.class)
    void testGetNearUserList_CacheMiss_FilteredListEmpty() {
        NearLocationRequestDTO nearLocationRequestDTO = new NearLocationRequestDTO();
        nearLocationRequestDTO.setUserId(userId);
        nearLocationRequestDTO.setLatitude(locationRequest.getLatitude());
        nearLocationRequestDTO.setLongitude(locationRequest.getLongitude());

        // Arrange
        when(nearByUserCache.getCachedNearbyUsers(userId))
            .thenReturn(null);
        
        when(nearByReq.getNearbySearchSync(nearLocationRequestDTO))
            .thenReturn(ResponseEntity.ok(new NearbySearchResultDto(userId, Arrays.asList("user456", "user789"))));

        when(filteredList.acceptNearbyUsers(any(NearbySearchResultDto.class)))
            .thenReturn(ResponseEntity.ok(new NearbySearchResultDto(userId, Arrays.asList())));

        NoNearbyUsersException exception = assertThrows(NoNearbyUsersException.class, () -> {
            nearUserList.getNearUserList(locationRequest, userId);
        });

        assertTrue(exception.getMessage().contains(userId));
        verify(nearByUserCache, never()).cacheNearbyUsers(anyString(), anyList());
    }
}
