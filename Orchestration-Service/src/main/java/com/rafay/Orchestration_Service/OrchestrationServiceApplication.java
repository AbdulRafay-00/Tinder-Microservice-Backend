package com.rafay.Orchestration_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrchestrationServiceApplication {

	public static void main(String[] arge) {
		SpringApplication.run(OrchestrationServiceApplication.class, arge);
	}

}
