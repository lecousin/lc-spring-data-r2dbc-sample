package com.example.auth.service;

import java.util.UUID;

import com.example.auth.service.dto.SessionDto;

import reactor.core.publisher.Mono;

public interface AuthService {

	Mono<SessionDto> authenticate(String username, String password);
	
	Mono<SessionDto> checkSession(UUID uuid, String username);
	
	Mono<SessionDto> renewSession(SessionDto session);
	
	Mono<Void> closeSession(SessionDto session);
	
}
