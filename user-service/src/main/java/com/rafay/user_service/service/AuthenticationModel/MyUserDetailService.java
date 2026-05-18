package com.rafay.user_service.service.AuthenticationModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rafay.user_service.db_entities.AuthCredentials;
import com.rafay.user_service.repository.AuthCredentialsRepository;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private AuthCredentialsRepository authRepository;
    @Override
    public UserDetails loadUserByUsername(String userEmail)  {
        AuthCredentials authCredentials = authRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        return new UserPrinciple(authCredentials);
    }
    
}
