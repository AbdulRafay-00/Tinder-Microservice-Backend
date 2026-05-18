package com.rafay.user_service.Kafka.LocationMicroServiceKafkaTopic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;
import com.rafay.user_service.dto.locationDto.NearbySearchEventDto;

@Service
public class KafkaNearbySearchProducer {


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void NearbySuggestion(String TOPIC_NAME, NearbySearchEventDto nearbySearchEventDto) {
        try {
            String json = objectMapper.writeValueAsString(nearbySearchEventDto);
            kafkaTemplate.send(TOPIC_NAME, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize nearby search request", e);
        }
    }
}