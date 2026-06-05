package com.rafay.user_service.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.rafay.user_service.dto.locationDto.NearbySearchRequestDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/nearby-search")
@Validated
public class NearbySearchController {

    @Autowired
    private com.rafay.user_service.service.NearbySearchService.NearbySearchProducer nearbySearchProducer;

    @PostMapping("/event")
    public ResponseEntity<String> publishNearbySearch(
            @Valid @RequestBody NearbySearchRequestDto nearbySearchRequestDto,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body("Missing user identity from gateway");
        }

        nearbySearchProducer.publishNearbySearch(userId, nearbySearchRequestDto);
        return ResponseEntity.ok("Nearby search request recorded");
    }
}