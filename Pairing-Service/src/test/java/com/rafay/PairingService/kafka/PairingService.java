package com.rafay.PairingService.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafay.PairingService.DB.PairDbId;
import com.rafay.PairingService.repository.PairDBRepository;
import com.rafay.PairingService.testconfig.ContainerInfo;
import com.rafay.PairingService.dto.PairingEventDto;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PairSwipeKafkaListenerIT extends ContainerInfo { // your renamed base class

    @Autowired
    private PairDBRepository pairDBRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.kafka.topics.pairing-topic}")
    private String pairingTopic;

    private KafkaTemplate<String, String> testProducer;

    private KafkaTemplate<String, String> buildTestProducer() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", kafka.getBootstrapServers()); // from your @Container field
        config.put("key.serializer", StringSerializer.class);
        config.put("value.serializer", StringSerializer.class); // plain string, matches listener's raw payload
        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(config);
        return new KafkaTemplate<>(pf);
    }

    @Test
    void perfectMatch_savesPairingRecord() throws Exception {
        testProducer = buildTestProducer();

        // Given: build the DTO myself, serialize to JSON string manually
        PairingEventDto event = new PairingEventDto("user-A", "user-B");
        String payload = objectMapper.writeValueAsString(event);

        // When: publish onto the real topic
        testProducer.send(pairingTopic, payload);

        // Then: wait for async consumption, verify DB write
        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            PairDbId id = new PairDbId("user-A", "user-B");
            assertThat(pairDBRepository.findById(id)).isPresent();
        });
    }
}