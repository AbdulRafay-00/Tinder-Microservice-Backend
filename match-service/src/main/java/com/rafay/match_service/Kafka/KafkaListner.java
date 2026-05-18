package com.rafay.match_service.Kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rafay.match_service.Dtos.SwipeEventRequest;
import com.rafay.match_service.Service.swipeService.SwipeService;

import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaListner {
    @Autowired
    SwipeService swipeService;

    @KafkaListener(topics = "swipe-events", groupId = "match-service-group")
    public void consumeSwipeEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("Received message: " + message);
        try {
            SwipeEventRequest eventreq = objectMapper.readValue(message, SwipeEventRequest.class);
            System.out.println("Parsed swipe event: " + eventreq);
            swipeService.saveSwipeEvent(eventreq);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize swipe event", e);
        }
        // Here you can add logic to process the swipe event, e.g., update matches in the database
    }
}

