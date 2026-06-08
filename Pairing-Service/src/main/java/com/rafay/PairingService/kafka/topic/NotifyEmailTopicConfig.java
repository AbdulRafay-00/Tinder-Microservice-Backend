package com.rafay.PairingService.kafka.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class NotifyEmailTopicConfig {

    @Bean
    public NewTopic notificationEmailEventsTopic() {
        return TopicBuilder.name("notification-email-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}