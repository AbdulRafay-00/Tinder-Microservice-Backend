package com.rafay.user_service.Kafka.kafkaConfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic nearbySearchTopic() {
        return TopicBuilder.name("nearby-search")
                .partitions(1)
                .replicas(1)
                .build();
    }
}