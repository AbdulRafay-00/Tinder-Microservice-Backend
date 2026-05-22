package com.rafay.match_service.Kafka.NearBySuggestionListner;


import tools.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rafay.match_service.Dtos.NearbySearchResultDto;

@Service
public class NearbySearchResultKafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
        topics = "nearby-result",
        groupId = "nearby-result-group"
    )
    public void consumeNearbyResult(String message) {
        try {
            NearbySearchResultDto result = objectMapper.readValue(message, NearbySearchResultDto.class);

            System.out.println("📥 Nearby result received for user: " + result.getUserId());
            System.out.println(result.getNearbyUserIds());
        } catch (Exception e) {
            System.err.println("❌ Error consuming nearby result: " + e.getMessage());
        }
    }
}