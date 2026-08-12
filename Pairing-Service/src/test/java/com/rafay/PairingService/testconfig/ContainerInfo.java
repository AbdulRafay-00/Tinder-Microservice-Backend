package com.rafay.PairingService.testconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
public abstract class ContainerInfo {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.43");

    @Container
    // @ServiceConnection
    protected
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:4.2.0"));

        @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        // add mysql equivalents here too if your manual config also reads datasource props via @Value
    }
}
//         @DynamicPropertySource
//     static void overrideProperties(DynamicPropertyRegistry registry) {
//         registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//         // wire MySQL container properties into Spring Boot test context
//         registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
//         registry.add("spring.datasource.username", mySQLContainer::getUsername);
//         registry.add("spring.datasource.password", mySQLContainer::getPassword);
//         registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
//         // ensure Hibernate creates schema for tests
//         registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
//     }
// }