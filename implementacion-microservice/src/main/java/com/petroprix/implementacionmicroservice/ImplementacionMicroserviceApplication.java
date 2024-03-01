package com.petroprix.implementacionmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ImplementacionMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImplementacionMicroserviceApplication.class, args);
	}

}
