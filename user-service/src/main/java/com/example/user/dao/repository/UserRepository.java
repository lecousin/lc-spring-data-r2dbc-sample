package com.example.user.dao.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.example.user.dao.model.User;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

	Mono<User> findByUsername(String username);

	Mono<User> findByUsernameAndPassword(String username, String password);
	
}
