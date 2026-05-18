package com.rafay.match_service.Service.JwtService;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtServices jwtServices;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String userId = jwtServices.extractUserId(token); // instance call, not static
                request.setAttribute("userId", userId);           // available in controllers
            } catch (Exception e) {
                // token is invalid or expired — reject the request
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // stop the filter chain
            }
        }

        filterChain.doFilter(request, response); // always call this at the end
    }
}