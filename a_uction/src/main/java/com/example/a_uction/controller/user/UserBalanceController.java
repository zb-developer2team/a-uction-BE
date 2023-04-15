package com.example.a_uction.controller.user;

import com.example.a_uction.model.user.dto.Balance;
import com.example.a_uction.service.user.UserBalanceService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/balance")
@RequiredArgsConstructor
public class UserBalanceController {

	private final UserBalanceService userBalanceService;

	@PutMapping("/charge")
	public ResponseEntity<Balance.Response> chargeBalance(@RequestBody Balance.Request balance,
		Principal principal) {
		return ResponseEntity.ok(userBalanceService.charge(principal.getName(), balance));
	}
}
