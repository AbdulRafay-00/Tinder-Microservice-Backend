package com.rafay.Notification_Service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

	@Bean
	public NewTopic pairingEventsTopic() {
		return TopicBuilder.name("pairing-events").partitions(3).build();
	}

	@Bean
	public NewTopic pairingEventsRetry1Topic() {
		return TopicBuilder.name("pairing-events-retry-1").partitions(3).build();
	}

	@Bean
	public NewTopic pairingEventsRetry2Topic() {
		return TopicBuilder.name("pairing-events-retry-2").partitions(3).build();
	}

	@Bean
	public NewTopic pairingEventsDltTopic() {
		return TopicBuilder.name("pairing-events-dlt").partitions(3).build();
	}
}