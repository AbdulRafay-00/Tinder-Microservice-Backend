package com.rafay.user_service.controller.LoginSignupController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafay.user_service.dto.LoginDto;
import com.rafay.user_service.service.LoginSignupService.LoginServices;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {
    @Mock
    private LoginServices loginServices;

    @InjectMocks
    private LoginController loginController;

    @Test
    void testUser_login() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("password");
        when(loginServices.verifyCredential(loginDto)).thenReturn("Login successful");
        String result = loginController.user_login(loginDto);

        verify(loginServices).verifyCredential(loginDto);
        assertEquals("Login successful", result);


    }

    @Test
    void testUser_login2() {
        
    }
}
