package com.example.user.service;

import com.example.user.service.dto.UserDto;

import reactor.core.publisher.Mono;

public interface UserService {

	/**
	 * Retrieve a User by its username and password.
	 * 
	 * @param username username
	 * @param password clear password
	 * @return the user if usernaame and password are valid
	 */
	Mono<UserDto> checkPassword(String username, String password);
	
	Mono<UserDto> createUser(String username, String password);
	
	Mono<UserDto> getUser(String username);
	
	Mono<Void> initDatabase();
	
}
