package com.rafay.locationService.controller;


import com.rafay.locationService.DTO.LocationRequestDTO;
import com.rafay.locationService.DTO.NearbySearchResultDto;
import com.rafay.locationService.Service.locationService.NearbySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private NearbySearchService nearbySearchService;

@PostMapping("/recommendations")
public ResponseEntity<List<String>> getRecommendations(
        @RequestBody LocationRequestDTO request) {

    List<String> nearby = nearbySearchService.processNearbySearchAsList(request);

    return ResponseEntity.ok(nearby);
}

@PostMapping("/nearby-search/sync")
public ResponseEntity<NearbySearchResultDto> getNearbySearchSync(
        @RequestBody LocationRequestDTO request) {

    NearbySearchResultDto result = nearbySearchService.processNearbySearch(request);
    return ResponseEntity.ok(result);
}
}