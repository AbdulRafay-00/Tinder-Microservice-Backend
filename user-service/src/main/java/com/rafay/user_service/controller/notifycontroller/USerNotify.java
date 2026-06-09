package com.rafay.user_service.controller.notifycontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafay.user_service.dto.NotifyDto;
import com.rafay.user_service.repository.AuthCredentialsRepository;
import com.rafay.user_service.repository.UserProfileDBRepository;

@RestController

public class USerNotify {
    @Autowired
    AuthCredentialsRepository authCredentialsRepository;
    @Autowired
    UserProfileDBRepository userProfileDBRepository;
    @PostMapping("/notify")
    public NotifyDto notifyUser(@Deprecated String userId) {
        // Logic to send notification to the user
        NotifyDto notifyDto = new NotifyDto();
        authCredentialsRepository.findById(userId).ifPresent(auth -> {
            notifyDto.setEmail(auth.getUserEmail());
        });
        userProfileDBRepository.findById(userId).ifPresent(profile -> {
            notifyDto.setUsername(profile.getName());
        });
        return notifyDto;
    }
}
