package com.rafay.user_service.controller.notifycontroller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafay.user_service.dto.NotifyDto;

@RestController

public class k {

    @PostMapping("/notify")
    public NotifyDto notifyUser() {
        // Logic to send notification to the user
        NotifyDto notifyDto = new NotifyDto();
        notifyDto.setEmail("user@example.com");
        notifyDto.setUsername("john_doe");
        return notifyDto;
    }
}
