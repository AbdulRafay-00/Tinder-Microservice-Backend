package com.rafay.locationService.Kafka.KafkaConfig;

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

    @Bean
    public NewTopic nearbyResultsTopic() {
        return TopicBuilder.name("nearby-results")
                .partitions(1)
                .replicas(1)
                .build();
    }
}