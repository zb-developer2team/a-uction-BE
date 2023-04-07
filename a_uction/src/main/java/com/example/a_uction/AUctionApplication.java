package com.example.a_uction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AUctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AUctionApplication.class, args);
	}

}
