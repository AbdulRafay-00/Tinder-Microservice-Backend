package com.rafay.user_service.Kafka.MatchServiceTopic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.rafay.user_service.dto.locationDto.LocataionDto;


import tools.jackson.databind.ObjectMapper;

public class kafkaSwipEventProducer {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void kafkaMessage(String topic, LocataionDto loc) {
        try {
            String jsonstr = objectMapper.writeValueAsString(loc); // DTO → JSON
            kafkaTemplate.send(topic, jsonstr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize swipe event", e);
        }
    }
}
