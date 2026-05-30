package com.rafay.Orchestration_Service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafay.Orchestration_Service.DTO.LocationRequset;
import com.rafay.Orchestration_Service.FeignClients.NearByUserEvent.NearByReq;
import com.rafay.Orchestration_Service.service.NearUserList;


@RestController
@RequestMapping("/discovery")
public class UserReq {
    @Autowired
    NearUserList nearUserList;
    @PostMapping
    public ResponseEntity<?> getDiscovery(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody LocationRequset locationRequest) {
                System.out.println("Received discovery request for user: " + userId);
                System.out.println("Location request: " + locationRequest);

                nearUserList.getNearUserList(locationRequest, userId);


                return ResponseEntity.ok("Discovery request received for user: " + userId +
                        " at location:");
        // ...
    }
}
