package com.rafay.user_service.service.notifyservice;


import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.db_entities.UserProfileDB;
import com.rafay.user_service.dto.NotifyDto;
import com.rafay.user_service.repository.AuthCredentialsRepository;
import com.rafay.user_service.repository.UserProfileDBRepository;

@ExtendWith(MockitoExtension.class)
public class NotifyServiceTest {

    @Mock
    AuthCredentialsRepository authRepository;

    @Mock
    UserProfileDBRepository profileRepository;

    @InjectMocks
    NotifyService notifyService;

    @Test
    void testGetNotifyData_nullUserId_returnsEmptyDto() {
        NotifyDto result = notifyService.getNotifyData(null);

        assertNull(result.getEmail());
        assertNull(result.getUsername());
        verifyNoInteractions(authRepository, profileRepository);
    }

    @Test
    void testGetNotifyData_blankUserId_returnsEmptyDto() {
        NotifyDto result = notifyService.getNotifyData("   ");

        assertNull(result.getEmail());
        assertNull(result.getUsername());
        verifyNoInteractions(authRepository, profileRepository);
    }

    @Test
    void testGetNotifyData_authNotFound_emailIsNull() {
        String userId = "user-123";
        UserProfileDB profile = new UserProfileDB();
        profile.setName("Rafay");

        when(authRepository.findById(userId)).thenReturn(Optional.empty());
        when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));

        NotifyDto result = notifyService.getNotifyData(userId);

        assertNull(result.getEmail());
        assertEquals("Rafay", result.getUsername());
    }

    @Test
    void testGetNotifyData_profileNotFound_nameIsNull() {
        String userId = "user-123";
        AuthCredentials auth = new AuthCredentials();
        auth.setUserEmail("rafay@example.com");

        when(authRepository.findById(userId)).thenReturn(Optional.of(auth));
        when(profileRepository.findById(userId)).thenReturn(Optional.empty());

        NotifyDto result = notifyService.getNotifyData(userId);

        assertEquals("rafay@example.com", result.getEmail());
        assertNull(result.getUsername());
    }

    @Test
    void testGetNotifyData_bothFound_returnsFullDto() {
        String userId = "user-123";
        AuthCredentials auth = new AuthCredentials();
        auth.setUserEmail("rafay@example.com");
        UserProfileDB profile = new UserProfileDB();
        profile.setName("Rafay");

        when(authRepository.findById(userId)).thenReturn(Optional.of(auth));
        when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));

        NotifyDto result = notifyService.getNotifyData(userId);

        assertEquals("rafay@example.com", result.getEmail());
        assertEquals("Rafay", result.getUsername());
    }
}