package com.rafay.user_service.service.JwtService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilters extends OncePerRequestFilter {

    @Autowired
    JwtServices jwtServices;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");
                String token = null;
                String userId = null;

                if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
                    token = authHeader.substring(7).trim();
                    if (!token.isBlank()) {
                        try {
                            userId = jwtServices.extractUserId(token);
                            if (userId != null && !userId.isBlank()) {
                                request.setAttribute("userId", userId);
                            }
                        } catch (Exception ex) {
                            // Keep request flowing; controller can return a clear auth error.
                            request.setAttribute("jwtError", "Invalid token");
                        }
                    }
                }

                // Optional authentication context: only set when userId is available.
                if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            userId, "", java.util.Collections.emptyList());
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null , userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                filterChain.doFilter(request, response);
    }
    
}
