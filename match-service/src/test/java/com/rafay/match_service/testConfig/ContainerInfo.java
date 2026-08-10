package com.rafay.match_service.testConfig;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {"spring.kafka.bootstrap-servers=localhost:9092", "spring.kafka.listener.auto-startup=false"})
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
public abstract class ContainerInfo {

    @Container
    @ServiceConnection
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.43");
    
}
