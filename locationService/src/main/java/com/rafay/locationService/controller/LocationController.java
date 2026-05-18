package com.rafay.locationService.controller;


import com.rafay.locationService.DTO.LocationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rafay.locationService.Service.locationService.LocationService;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

@PostMapping("/recommendations")
public ResponseEntity<List<String>> getRecommendations(
        @RequestBody LocationRequestDTO request) {

    List<String> nearby = locationService.getRecommendations(
        request.getUserId(),
        request.getLatitude(),
        request.getLongitude()
    );

    return ResponseEntity.ok(nearby);
}
}