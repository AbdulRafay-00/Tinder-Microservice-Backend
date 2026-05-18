package com.rafay.locationService.Kafka.NearbySearchSuggestion;

import tools.jackson.databind.ObjectMapper;

import com.rafay.locationService.DTO.NearbySearchResultDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NearbySearchKafkaProducer {

	private static final String TOPIC = "nearby-result";

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public void publishNearbyResult(NearbySearchResultDto result) {
		try {
			String payload = objectMapper.writeValueAsString(result);
			kafkaTemplate.send(TOPIC, result.getUserId(), payload);
			System.out.println("📤 Published nearby result for user: " + result.getUserId());
		} catch (Exception e) {
			System.err.println("❌ Failed to publish nearby result: " + e.getMessage());
		}
	}
}
