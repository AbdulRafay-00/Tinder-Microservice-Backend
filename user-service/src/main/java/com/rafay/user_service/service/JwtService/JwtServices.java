package com.rafay.user_service.service.JwtService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.db_entities.UserProfileDB;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServices {

    @Value("${secret.key}")
    String secretKey;

    public String jwt_token_gen(AuthCredentials AuthCredentials, UserProfileDB userProfile) {

        HashMap<String, Object> clai = new HashMap<>();
        clai.put("name", userProfile.getName());
        return Jwts.builder()
                .claims()
                .add(clai)
                .subject(AuthCredentials.getUserId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .and()
                .signWith(keygeb())
                .compact();
    }

    private SecretKey keygeb() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Claims extraAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(keygeb())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraAllClaims(token);
        return claimsResolver.apply(claims);

    }

    // extraction of payload values
    public String extractUserId(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private boolean isTokenExpire(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public String extractRole(String token) {
        return extractClaims(token, claims -> (String) claims.get("role"));
    }

    // validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return (userId.equals(userDetails.getUsername()) && !isTokenExpire(token));
    }

}
