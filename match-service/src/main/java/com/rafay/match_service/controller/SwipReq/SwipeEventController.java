package com.rafay.match_service.controller.SwipReq;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafay.match_service.Dtos.SwipeEventRequest;
import com.rafay.match_service.Service.swipeService.SwipeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/swipe")
@RequiredArgsConstructor
public class SwipeEventController {

    private final SwipeService swipeService;

    @PostMapping("/events")
    public ResponseEntity<String> acceptSwipeEvent(@RequestBody SwipeEventRequest request,
        @RequestHeader(value = "X-User-Id") String swiperId) {
        swipeService.saveSwipeEvent(request, swiperId);
        return ResponseEntity.ok("Swipe event processed successfully");
    }
}