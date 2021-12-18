package com.example.auth.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.auth.api.dto.LoginRequest;
import com.example.auth.service.AuthService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	@PostMapping("/login")
	public Mono<String> login(@Valid @RequestBody LoginRequest request) {
		return authService.authenticate(request.getUsername(), request.getPassword())
			.map(tokenProvider::generateToken)
			.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password")));
	}

}
