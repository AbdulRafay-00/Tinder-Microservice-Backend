package com.rafay.user_service.service.LoginSignupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.db_entities.UserProfileDB;
import com.rafay.user_service.dto.LoginDto;
import com.rafay.user_service.repository.AuthCredentialsRepository;
import com.rafay.user_service.service.JwtService.JwtServices;
@Service
public class LoginServices {

    @Autowired
    private JwtServices jwtServices;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthCredentialsRepository authCredentialsRepository; // ✅ ADD THIS

    public String verifyCredential(LoginDto loginDto) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (Exception e) {
            return "Invalid email or password";
        }

        // ✅ Fetch real user from DB instead of empty objects
        AuthCredentials authCredentials = authCredentialsRepository
                .findByUserEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDB userProfileDB = authCredentials.getUserProfileDB();

        return jwtServices.jwt_token_gen(authCredentials, userProfileDB);
    }
}
