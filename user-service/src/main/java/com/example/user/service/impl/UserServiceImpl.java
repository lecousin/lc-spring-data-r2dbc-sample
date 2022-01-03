package com.example.user.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.user.dao.model.User;
import com.example.user.dao.repository.UserRepository;
import com.example.user.service.UserService;
import com.example.user.service.dto.UserDto;

import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	@Qualifier("userOperations")
	private LcR2dbcEntityTemplate template;
	
	@Override
	public Mono<UserDto> checkPassword(String username, String password) {
		return Mono.just(Tuples.of(username, password))
			.map(tuple -> Tuples.of(tuple.getT1(), encrypt(tuple.getT2())))
			.flatMap(tuple -> userRepo.findByUsernameAndPassword(tuple.getT1(), tuple.getT2()))
			.map(UserDto::fromEntity);
	}
	
	@Override
	public Mono<UserDto> createUser(String username, String password, boolean admin) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(encrypt(password));
		user.setAdmin(admin);
		return userRepo.save(user).map(UserDto::fromEntity);
	}
	
	@Override
	public Mono<UserDto> getUser(String username) {
		return userRepo.findByUsername(username).map(UserDto::fromEntity);
	}
	
	@Override
	public Mono<UserDto> getUser(long userId) {
		return userRepo.findById(userId).map(UserDto::fromEntity);
	}
	
	@Override
	public Flux<UserDto> getUsers() {
		return userRepo.findAll().map(UserDto::fromEntity);
	}
	
	@Override
	public Mono<UserDto> changePassword(String username, String newPassword) {
		return userRepo.findByUsername(username)
			.doOnNext(user -> user.setPassword(encrypt(newPassword)))
			.flatMap(userRepo::save)
			.map(UserDto::fromEntity);
	}
	
	private static final char[] generatePasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,;.:!+=()[]".toCharArray();
	
	@Override
	public Mono<String> resetPassword(long userId) {
		return userRepo.findById(userId)
			.flatMap(user -> {
				Random random = new Random();
				StringBuilder s = new StringBuilder(10);
				for (int i = 0; i < 10; ++i)
					s.append(generatePasswordChars[random.nextInt(generatePasswordChars.length)]);
				String generatedPassword = s.toString();
				user.setPassword(encrypt(generatedPassword));
				return userRepo.save(user).thenReturn(generatedPassword);
			});
	}
	
	@Override
	public Mono<UserDto> setAdministrator(long userId, boolean admin) {
		return userRepo.findById(userId)
			.doOnNext(user -> user.setAdmin(admin))
			.flatMap(userRepo::save)
			.map(UserDto::fromEntity);
	}
	
	private static String encrypt(String clear) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(clear.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hashed);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Cannot encrypt password", e);
		}
	}
	
}
