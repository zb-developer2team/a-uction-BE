package com.example.a_uction.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SmsVerifyConfig {

	@Bean
	public HttpHeaders headers() {
		return new HttpHeaders();
	}

}
