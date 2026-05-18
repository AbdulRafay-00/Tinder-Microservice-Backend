package com.rafay.user_service.controller.LoginSignupController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafay.user_service.dto.SignupDto;
import com.rafay.user_service.service.LoginSignupService.SignupService;

@RestController
@RequestMapping("/signup")
public class SignupController {
    
    @Autowired
    private SignupService SignupService;
    
    @PostMapping("/service")
    public String signup(@RequestBody SignupDto signupDto) {
        return SignupService.signup(signupDto);
    }
}
