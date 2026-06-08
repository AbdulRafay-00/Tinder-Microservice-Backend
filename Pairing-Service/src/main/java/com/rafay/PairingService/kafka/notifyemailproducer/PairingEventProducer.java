package com.rafay.PairingService.kafka.notifyemailproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.PairingService.dto.PairingEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PairingEventProducer {
    private static final Logger log = LoggerFactory.getLogger(PairingEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic notificationEmailEventsTopic;
    
    public void sendPairingEvent(String swiperId, String swipedId) {
        PairingEventDto event = new PairingEventDto(swiperId, swipedId);
        try {
            String payload = objectMapper.writeValueAsString(event);
            String topicName = notificationEmailEventsTopic.name();
            kafkaTemplate.send(topicName, payload);
            log.info("Pairing event sent to Kafka - topic: {}, swiper: {}, swiped: {}", topicName, swiperId, swipedId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize pairing event — swiper: {}, swiped: {}", swiperId, swipedId, e);
        }
    }
}