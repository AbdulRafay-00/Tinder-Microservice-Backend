package com.rafay.user_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.rafay.user_service.dto.swipLogicDto.SwipEventFrontendDto;
import com.rafay.user_service.service.SwipService.ProducerSwipService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/swipe")
public class SwipeController {
    @Autowired
    private ProducerSwipService producer;
    @PostMapping("/event")
    public ResponseEntity<String> processSwipeEvent(@RequestBody SwipEventFrontendDto swipEventFrontendDto,
        HttpServletRequest httpRequest
    ){
        String swiperId = (String) httpRequest.getAttribute("userId"); // from JWT filter
        System.out.println("Processing swipe controller userId=" + swiperId);
        System.out.println("DTO received: " + swipEventFrontendDto);
        
        if (swiperId == null || swiperId.isBlank()) {
            System.err.println("ERROR: No userId in request. Check Authorization header and JWT filter.");
            return ResponseEntity.badRequest().body("Missing or invalid JWT token");
        }
        
        if (swipEventFrontendDto == null || swipEventFrontendDto.getSwipedId() == null) {
            System.err.println("ERROR: Invalid request body. swipedId is required.");
            return ResponseEntity.badRequest().body("Invalid request: swipedId required");
        }
        
        System.out.println("Received swipe: swipedId=" + swipEventFrontendDto.getSwipedId() + ", direction=" + swipEventFrontendDto.getSwipeDirection() + ", swiperId=" + swiperId);
        producer.processSwipe(swiperId, swipEventFrontendDto);
        return ResponseEntity.ok("Swipe recorded");
    }
}
