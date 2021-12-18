package com.example.user.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.user.dao.model.User;
import com.example.user.dao.repository.UserRepository;
import com.example.user.service.UserService;
import com.example.user.service.dto.UserDto;

import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import net.lecousin.reactive.data.relational.schema.RelationalDatabaseSchema;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	@Qualifier("userOperations")
	private LcR2dbcEntityTemplate template;
	
	@PostConstruct
	public void initDatabase() {
		Mono<Void> createDatabase;
		if (System.getProperty("createDatabase") != null) {
			RelationalDatabaseSchema schema = template.getLcClient().buildSchemaFromEntities(Arrays.asList(User.class));
			createDatabase = template.getLcClient().createSchemaContent(schema);
		} else {
			createDatabase = Mono.empty();
		}
		createDatabase.then(
			userRepo.findByUsername("test")
			.map(UserServiceImpl::dto)
			.switchIfEmpty(Mono.defer(() -> createUser("test", "test")))
		).subscribe();
	}
	
	@Override
	public Mono<UserDto> checkPassword(String username, String password) {
		return Mono.just(Tuples.of(username, password))
			.map(tuple -> Tuples.of(tuple.getT1(), encrypt(tuple.getT2())))
			.flatMap(tuple -> userRepo.findByUsernameAndPassword(tuple.getT1(), tuple.getT2()))
			.map(UserServiceImpl::dto);
	}
	
	@Override
	public Mono<UserDto> createUser(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(encrypt(password));
		return userRepo.save(user).map(UserServiceImpl::dto);
	}
	
	private static UserDto dto(User user) {
		UserDto dto = new UserDto();
		dto.setUsername(user.getUsername());
		return dto;
	}
	
	@Override
	public Mono<UserDto> getUser(String username) {
		return userRepo.findByUsername(username).map(UserServiceImpl::dto);
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
