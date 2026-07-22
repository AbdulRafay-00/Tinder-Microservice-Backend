package com.rafay.user_service.service.AuthenticationModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.repository.AuthCredentialsRepository;

@ExtendWith(MockitoExtension.class)
public class MyUserDetailServiceTest {
    @Mock
    private AuthCredentialsRepository authRepository;

    @InjectMocks
    MyUserDetailService myUserDetailService;

    @Test
    void testLoadUserByUsername() {
        AuthCredentials o = new AuthCredentials("rafay", "rafay@example.com");
        when( authRepository.findByUserEmail(o.getUserEmail()))
        .thenReturn(Optional.of(o));

// Act
        UserDetails result =  myUserDetailService.loadUserByUsername("rafay");

// assert
        assertEquals(o.getUserEmail(), result.getUsername());
        



        System.out.println("Test for loadUserByUsername");
    }


// unknown user case
}
