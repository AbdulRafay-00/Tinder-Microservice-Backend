package com.rafay.PairingService.kafka.notifyemailproducer;

import com.rafay.PairingService.testconfig.ContainerInfo;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PairingEventProducerTest extends ContainerInfo {

    @Autowired
    private PairingEventProducer pairingEventProducer;

    @Autowired
    private NewTopic notificationEmailEventsTopic;

    private Consumer<String, String> testConsumer;

    @Test
    void sendPairingEvent_landsOnNotificationTopic() {
        String topicName = notificationEmailEventsTopic.name();

        // STEP 1: set up a plain reader on the real topic — a stand-in for "someone checking Kafka"
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "verify-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        testConsumer = new DefaultKafkaConsumerFactory<String, String>(props).createConsumer();
        testConsumer.subscribe(Collections.singletonList(topicName));

        // STEP 2: call your real producer bean — no mocks, no fakes
        pairingEventProducer.sendPairingEvent("user-C", "user-D");

        // STEP 3: poll the topic for up to 10 seconds, see what actually landed there
        ConsumerRecords<String, String> records = testConsumer.poll(Duration.ofSeconds(10));

        // STEP 4: the only thing we're checking — did exactly one message actually arrive?
        assertThat(records.count()).isEqualTo(1);
    }

    @AfterEach
    void closeConsumer() {
        if (testConsumer != null) testConsumer.close();
    }
}