package com.example.a_uction.controller;

import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/get-time")
public class TimeController {
	@GetMapping
	public ResponseEntity<LocalDateTime> getServerTime() {
		return ResponseEntity.ok(LocalDateTime.now());
	}

}
