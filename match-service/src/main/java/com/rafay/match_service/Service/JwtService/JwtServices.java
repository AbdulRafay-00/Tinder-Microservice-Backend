package com.rafay.match_service.Service.JwtService;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServices {

    @Value("${secret.key}")
    String secretKey;

    private SecretKey getSigningKey() {
        byte[] key = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)  // this automatically validates signature + expiry
            .getPayload();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }
}