package com.example.user.service;

import reactor.core.publisher.Mono;

public interface UserDatabaseInitializer {

	Mono<Void> initSchema();
	
	Mono<Void> createUsers();
	
}
