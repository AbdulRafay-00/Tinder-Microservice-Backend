package com.rafay.user_service.controller.notifycontroller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.user_service.dto.NotifyDto;
import com.rafay.user_service.dto.notifydto.notifyFrontEndDto;
import com.rafay.user_service.service.notifyservice.NotifyService;

@ExtendWith(MockitoExtension.class)
class UserNotifyTest {

    @Mock
    private NotifyService notifyService;

    @InjectMocks
    private UserNotify userNotify;

    @Test
    @DisplayName("Should return notify data when request has valid userId")
    void notifyUser_validUserId_returnsNotifyData() {
        // Arrange
        notifyFrontEndDto request = new notifyFrontEndDto();
        request.setUserId("user-123");

        NotifyDto expected = new NotifyDto();
        when(notifyService.getNotifyData("user-123")).thenReturn(expected);

        // Act
        NotifyDto result = userNotify.notifyUser(request);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(notifyService).getNotifyData("user-123");
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void notifyUser_nullRequest_passesNullUserId() {
        NotifyDto expected = new NotifyDto();
        when(notifyService.getNotifyData(null)).thenReturn(expected);

        NotifyDto result = userNotify.notifyUser(null);

        assertThat(result).isEqualTo(expected);
        verify(notifyService).getNotifyData(null);
    }
}