package com.rafay.user_service.service.LoginSignupService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.dto.LoginDto;
import com.rafay.user_service.repository.AuthCredentialsRepository;
import com.rafay.user_service.service.JwtService.JwtServices;

@ExtendWith(MockitoExtension.class)
public class LoginServicesTest {

    @Mock
    private JwtServices jwtServices;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthCredentialsRepository authCredentialsRepository;

    @InjectMocks
    private LoginServices loginServices;

    @Test
    void testVerifyCredential() {
        AuthCredentials authCredentials = new AuthCredentials( "rafay@example.com", "12345");
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("rafay@example.com");
        loginDto.setPassword("12345");

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword()))).thenReturn(null);

        when(authCredentialsRepository.findByUserEmail(
            loginDto.getEmail())).thenReturn(Optional.of(authCredentials));


        when(jwtServices.jwt_token_gen(authCredentials, authCredentials.getUserProfileDB()))
                .thenReturn("mocked-jwt-token");



        String result = loginServices.verifyCredential(loginDto);

        assertEquals("mocked-jwt-token", result);


    }





    
}
