package com.example.a_uction.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
	@GetMapping
	public HttpStatus healthCheck() {
		return HttpStatus.OK;
	}
}
