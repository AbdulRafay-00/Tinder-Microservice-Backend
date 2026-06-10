package com.rafay.Notification_Service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.Notification_Service.controller.UserServiceClient;
import com.rafay.Notification_Service.dto.MatchEventDto;
import com.rafay.Notification_Service.dto.UserDto;
import com.rafay.Notification_Service.dto.UserServiceClientDto;
import com.rafay.Notification_Service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchEventListener {

    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final EmailService emailService;
    UserServiceClientDto userServiceClientDto;

    @RetryableTopic(attempts = "4", backOff = @BackOff(delay = 5000, multiplier = 2.0), 
    topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "${app.kafka.topics.match-topic}", groupId = "${app.kafka.group-id}", 
    containerFactory = "mainContainerFactory")
    public void listen(String message) {
        try {
            log.info("Match event received: {}", message);

            MatchEventDto event = objectMapper.readValue(message, MatchEventDto.class);

            UserDto swiper = userServiceClient.getUserById(new UserServiceClientDto(event.swiperId()));
            UserDto swiped = userServiceClient.getUserById(new UserServiceClientDto(event.swipedId()));

            emailService.sendMatchEmail(swiper.email(), swiped.name(), swiper.name());
            emailService.sendMatchEmail(swiped.email(), swiper.name(), swiped.name());

            log.info("Match emails sent to {} and {}", swiper.email(), swiped.email());

        } catch (Exception e) {
            log.error("Failed to process match event: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @DltHandler
    public void handleDlt(String message) {
        log.error("Permanent failure — all retries exhausted. Message: {}", message);
    }
}