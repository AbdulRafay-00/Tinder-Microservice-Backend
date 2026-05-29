package com.rafay.Orchestration_Service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafay.Orchestration_Service.DTO.LocationRequset;


@RestController
@RequestMapping("/discovery")
public class UserReq {
    
    @PostMapping
    public ResponseEntity<?> getDiscovery(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody LocationRequset locationRequest) {



                return ResponseEntity.ok("Discovery request received for user: " + userId +
                        " at location:");
        // ...
    }
}
