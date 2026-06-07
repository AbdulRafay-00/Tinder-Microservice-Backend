package com.rafay.match_service.Kafka.PairingEventProducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.match_service.Dtos.PairingEventDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PairingEventProducer {
    @Value("${app.kafka.topics.pairing-topic}")
    private final String PAIRING_TOPIC;
    private static final Logger log = LoggerFactory.getLogger(PairingEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishPairingEvent(String swiperId, String swipedId) {
        PairingEventDto event = new PairingEventDto(swiperId, swipedId);

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PAIRING_TOPIC, payload);
            log.info("Pairing event sent to Kafka — topic: {}, swiper: {}, swiped: {}", PAIRING_TOPIC, swiperId, swipedId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize pairing event — swiper: {}, swiped: {}", swiperId, swipedId, e);
        }
    }
}