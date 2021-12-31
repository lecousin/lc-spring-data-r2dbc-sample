package com.example.user.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.user.dao.model.User;
import com.example.user.service.UserDatabaseInitializer;
import com.example.user.service.UserService;

import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import net.lecousin.reactive.data.relational.schema.RelationalDatabaseSchema;
import reactor.core.publisher.Mono;

@Service
public class UserDatabaseInitializerImpl implements UserDatabaseInitializer {

	@Autowired
	@Qualifier("userOperations")
	private LcR2dbcEntityTemplate template;
	
	@Autowired
	private UserService service;

	
	@Override
	public Mono<Void> initSchema() {
		RelationalDatabaseSchema schema = template.getLcClient().buildSchemaFromEntities(Arrays.asList(User.class));
		return template.getLcClient().dropCreateSchemaContent(schema);
	}
	
	@Override
	public Mono<Void> createUsers() {
		return Mono.when(service.createUser("test", "test", true), service.createUser("guest", "guest", false));
	}

}
