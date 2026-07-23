package com.rafay.user_service.service.JwtService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.db_entities.UserProfileDB;
import com.rafay.user_service.service.AuthenticationModel.UserPrinciple;

import io.jsonwebtoken.Jwts;

public class JwtServicesTest {
    JwtServices jwts;

    @BeforeEach
    void setUp() {
        jwts = new JwtServices();
        ReflectionTestUtils.setField(jwts, "secretKey", "a-long-enough-test-secret-key-for-hmac-256-min-32-chars");
    }


    @Test
    void testExtractRole() {
        // HashMap<String, Object> clai = new HashMap<>();
        AuthCredentials authCredentials = new AuthCredentials("rafay@example.com", "12345");
        UserProfileDB userP = new UserProfileDB("Rafay", "0899999", 0,"ss", "ww", "Male", "pp");
        String result = jwts.jwt_token_gen(authCredentials, userP);
        
        assertNotNull(result);
    }
    
}
