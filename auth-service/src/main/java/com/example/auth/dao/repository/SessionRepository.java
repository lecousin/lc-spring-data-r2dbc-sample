package com.example.auth.dao.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.example.auth.dao.model.Session;

import reactor.core.publisher.Mono;

public interface SessionRepository extends R2dbcRepository<Session, UUID> {

	Mono<Void> deleteByExpirationLessThan(Instant before);
	
}
