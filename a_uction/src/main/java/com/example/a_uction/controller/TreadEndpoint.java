package com.example.a_uction.controller;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "thread")
public class TreadEndpoint {

	@ReadOperation
	public int threadCount() {
		return Thread.activeCount();
	}
}
