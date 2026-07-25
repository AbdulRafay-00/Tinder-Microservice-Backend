package com.rafay.user_service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.rafay.user_service.dto.swipLogicDto.SwipEventFrontendDto;
import com.rafay.user_service.service.SwipService.ProducerSwipService;

@ExtendWith(MockitoExtension.class)
class SwipeControllerTest {

    @Mock
    private ProducerSwipService producer;

    @InjectMocks
    private SwipeController swipeController;

    @Test
    @DisplayName("Should process swipe and return 200 when userId and swipedId are present")
    void processSwipeEvent_validRequest_returnsOk() {
        // Arrange
        SwipEventFrontendDto dto = new SwipEventFrontendDto();
        dto.setSwipedId("swiped-456");
        String swiperId = "swiper-123";

        // Act
        ResponseEntity<String> response = swipeController.processSwipeEvent(dto, swiperId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Swipe recorded");
        verify(producer).processSwipe(swiperId, dto);
    }

    @Test
    @DisplayName("Should return 400 and skip processing when userId header is missing")
    void processSwipeEvent_missingUserId_returnsBadRequest() {
        SwipEventFrontendDto dto = new SwipEventFrontendDto();
        dto.setSwipedId("swiped-456");

        ResponseEntity<String> response = swipeController.processSwipeEvent(dto, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Missing user identity from gateway");
        verify(producer, never()).processSwipe(anyString(), any());
    }

    @Test
    @DisplayName("Should return 400 and skip processing when swipedId is missing")
    void processSwipeEvent_missingSwipedId_returnsBadRequest() {
        SwipEventFrontendDto dto = new SwipEventFrontendDto();
        // swipedId intentionally left null

        ResponseEntity<String> response = swipeController.processSwipeEvent(dto, "swiper-123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid request: swipedId required");
        verify(producer, never()).processSwipe(anyString(), any());
    }
}