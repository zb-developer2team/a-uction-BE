package com.example.a_uction.controller;

import com.example.a_uction.model.TimeForm;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/get-time")
public class TimeController {
	@GetMapping
	public ResponseEntity<TimeForm> getServerTime() {

		LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
		return ResponseEntity.ok(TimeForm.builder().remainTime(
			LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
		).build());
	}

}
