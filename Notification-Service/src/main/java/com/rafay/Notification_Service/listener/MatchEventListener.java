package com.rafay.Notification_Service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.Notification_Service.client.UserServiceClient;
import com.rafay.Notification_Service.dto.MatchEventDto;
import com.rafay.Notification_Service.dto.UserDto;
import com.rafay.Notification_Service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchEventListener {

	private final ObjectMapper objectMapper;
	private final UserServiceClient userServiceClient;
	private final EmailService emailService;
	private final KafkaTemplate<String, String> kafkaTemplate;

	@KafkaListener(groupId = "${app.kafka.group-id}", topics = "${app.kafka.topics.match-topic}")
	public void listenMain(String message) {
		handleMessage(message, "pairing-events-retry-1", "main");
	}

	@KafkaListener(groupId = "${app.kafka.group-id}", topics = "pairing-events-retry-1")
	public void listenRetry1(String message) {
		handleMessage(message, "pairing-events-retry-2", "retry-1");
	}

	@KafkaListener(groupId = "${app.kafka.group-id}", topics = "pairing-events-retry-2")
	public void listenRetry2(String message) {
		handleMessage(message, "pairing-events-dlt", "retry-2");
	}

	@KafkaListener(groupId = "${app.kafka.group-id}", topics = "pairing-events-dlt")
	public void listenDlt(String message) {
		log.error("Permanent failure received on DLT: {}", message);
	}

	private void handleMessage(String rawMessage, String nextTopic, String stage) {
		try {
			log.info("Received {} event: {}", stage, rawMessage);
			MatchEventDto event = objectMapper.readValue(rawMessage, MatchEventDto.class);
			UserDto swiper = userServiceClient.getUserById(event.swiperId());
			UserDto swiped = userServiceClient.getUserById(event.swipedId());
			log.info("Fetched users for match: {} and {}", swiper.id(), swiped.id());
			emailService.sendMatchEmail(swiper.email(), swiped.name());
			emailService.sendMatchEmail(swiped.email(), swiper.name());
			log.info("Processed {} event successfully", stage);
		} catch (Exception exception) {
			log.error("Failed to process {} event. Forwarding to {}", stage, nextTopic, exception);
			forwardWithDelay(rawMessage, nextTopic);
		}
	}

	private void forwardWithDelay(String rawMessage, String nextTopic) {
		try {
			Thread.sleep(5000);
			kafkaTemplate.send(nextTopic, rawMessage);
			log.info("Forwarded failed message to {}", nextTopic);
		} catch (InterruptedException interruptedException) {
			Thread.currentThread().interrupt();
			log.error("Retry delay interrupted while forwarding to {}", nextTopic, interruptedException);
		} catch (Exception exception) {
			log.error("Failed to forward message to {}", nextTopic, exception);
		}
	}
}