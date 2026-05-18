package com.rafay.user_service.service.AuthenticationModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rafay.user_service.db_entities.AuthCredentials;


public class UserPrinciple implements UserDetails{

    private AuthCredentials authCredentials;
    public UserPrinciple( AuthCredentials authCredentials) {
        this.authCredentials = authCredentials;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPassword() {
        return authCredentials.getUserPassword();
    }

    @Override
    public String getUsername() {
        return authCredentials.getUserEmail();
    }
    
}
