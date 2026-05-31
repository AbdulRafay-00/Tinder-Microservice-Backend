package com.rafay.match_service.controller;

import com.rafay.match_service.Dtos.NearbySearchResultDto;
import com.rafay.match_service.Service.UserFilterService.FilterSuggestion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filter/nearbysearch")
public class NearbySearchController {
    @Autowired
    FilterSuggestion filterSuggestion;

    @PostMapping("/list")
    public ResponseEntity<NearbySearchResultDto> acceptNearbyUsers(@RequestBody NearbySearchResultDto request) {

        System.out.println("Received nearby search result for user: " + request.getUserId());
        NearbySearchResultDto  filteredList = filterSuggestion.filterSwiped(request);

        System.out.println("Filtered nearby users for user: " + request.getUserId()+"\n list");
        System.out.println(filteredList.getNearbyUserIds());
        System.out.println("\nFiltered list size: " + filteredList.getNearbyUserIds().size());
        return ResponseEntity.ok(filteredList);
    }
}