package com.rafay.user_service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rafay.user_service.dto.locationDto.NearbySearchRequestDto;
import com.rafay.user_service.service.NearbySearchService.NearbySearchProducer;

@ExtendWith(MockitoExtension.class)
class NearbySearchControllerTest {

    @Mock
    private NearbySearchProducer nearbySearchProducer;

    @InjectMocks
    private NearbySearchController nearbySearchController;

    @Test
    @DisplayName("Should publish event and return 200 when userId header is present")
    void publishNearbySearch_validUserId_returnsOk() {
        // Arrange
        NearbySearchRequestDto requestDto = new NearbySearchRequestDto();
        String userId = "user-123";

        // Act
        ResponseEntity<String> response = nearbySearchController.publishNearbySearch(requestDto, userId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Nearby search request recorded");
        verify(nearbySearchProducer).publishNearbySearch(userId, requestDto);
    }

    @Test
    @DisplayName("Should return 400 and skip publishing when userId header is missing")
    void publishNearbySearch_missingUserId_returnsBadRequest() {
        NearbySearchRequestDto requestDto = new NearbySearchRequestDto();

        ResponseEntity<String> response = nearbySearchController.publishNearbySearch(requestDto, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Missing user identity from gateway");
        verify(nearbySearchProducer, never()).publishNearbySearch(anyString(), any());
    }

    @Test
    @DisplayName("Should return 400 and skip publishing when userId header is blank")
    void publishNearbySearch_blankUserId_returnsBadRequest() {
        NearbySearchRequestDto requestDto = new NearbySearchRequestDto();

        ResponseEntity<String> response = nearbySearchController.publishNearbySearch(requestDto, "   ");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(nearbySearchProducer, never()).publishNearbySearch(anyString(), any());
    }
}