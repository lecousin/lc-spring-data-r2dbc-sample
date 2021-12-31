package com.example.book.service;

import reactor.core.publisher.Mono;

public interface BookDatabaseInitializer {

	Mono<Void> initSchema();
	
	Mono<Void> createAuthors();
	
	Mono<Void> createPublishers();
	
	Mono<Void> createBooks();
	
}
