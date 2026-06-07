package com.rafay.PairingService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.rafay.PairingService.dto.PairingEventDto;
import com.rafay.PairingService.service.PairingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PairSwipeKafkaListener {

    private final ObjectMapper objectMapper;
    private final PairingService pairingService;
    @KafkaListener(topics = "${app.kafka.topics.pairing-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String payload) {
        try {
            PairingEventDto event = objectMapper.readValue(payload, PairingEventDto.class);
            pairingService.savePairingEvent(event.swiperId(), event.swipedId());
            log.info("Saved swipe pairing: swiperId={}, swipedId={}", event.swiperId(), event.swipedId());
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize pairing event payload: {}", payload, e);
        }
    }
}