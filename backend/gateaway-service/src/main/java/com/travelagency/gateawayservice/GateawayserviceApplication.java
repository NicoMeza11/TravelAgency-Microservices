package com.travelagency.gateawayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GateawayserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GateawayserviceApplication.class, args);
	}

}
