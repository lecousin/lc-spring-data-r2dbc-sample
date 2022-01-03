package com.example.user.service;

import com.example.user.service.dto.UserDto;

import reactor.core.publisher.Flux;
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
	
	Mono<UserDto> createUser(String username, String password, boolean admin);
	
	Mono<UserDto> getUser(String username);
	
	Mono<UserDto> getUser(long userId);
	
	Flux<UserDto> getUsers();
	
	Mono<String> resetPassword(long userId);
	
	Mono<UserDto> setAdministrator(long userId, boolean admin);
	
	Mono<UserDto> changePassword(String username, String newPassword);
	
}
