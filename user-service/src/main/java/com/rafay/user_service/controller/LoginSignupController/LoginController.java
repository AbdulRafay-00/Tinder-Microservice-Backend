package com.rafay.user_service.controller.LoginSignupController;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rafay.user_service.dto.LoginDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.rafay.user_service.service.LoginSignupService.LoginServices;

@RestController
@RequestMapping("/login")
public class LoginController {
    LoginServices loginServices;
    LoginController(LoginServices loginServices) {
        this.loginServices = loginServices;
    }


    @PostMapping("/portal")
    public String user_login(@RequestBody LoginDto loginDto) {
        //TODO: process POST request
        
        return loginServices.verifyCredential(loginDto);
    }
    
    
}
