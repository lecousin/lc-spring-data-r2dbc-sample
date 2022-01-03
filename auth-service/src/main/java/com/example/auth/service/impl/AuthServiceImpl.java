package com.example.auth.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.auth.dao.model.Session;
import com.example.auth.dao.repository.SessionRepository;
import com.example.auth.service.AuthService;
import com.example.auth.service.dto.SessionDto;
import com.example.user.service.UserService;
import com.example.user.service.dto.UserDto;

import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import net.lecousin.reactive.data.relational.schema.RelationalDatabaseSchema;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
public class AuthServiceImpl implements AuthService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SessionRepository sessionRepo;
	
	@Value("${auth.expiration:60}")
	private long expirationMinutes;
	
	@Autowired
	@Qualifier("authOperations")
	private LcR2dbcEntityTemplate template;
	
	@PostConstruct
	public void initDatabase() {
		RelationalDatabaseSchema schema = template.getLcClient().buildSchemaFromEntities(Arrays.asList(Session.class));
		template.getLcClient().createSchemaContent(schema).subscribe();
	}
	
	@Override
	public Mono<SessionDto> authenticate(String username, String password) {
		return userService.checkPassword(username, password)
		.flatMap(user -> createSession(user).map(session -> Tuples.of(session, user)))
		.map(tuple -> createDto(tuple.getT1(), tuple.getT2()));
	}
	
	@Override
	public Mono<SessionDto> checkSession(UUID uuid, String username) {
		return sessionRepo.findById(uuid)
		.flatMap(session -> {
			if (!session.getUsername().equals(username) || session.getExpiration().isBefore(Instant.now()))
				return Mono.empty();
			logger.info("Session accepted for user {}", username);
			return userService.getUser(username)
				.map(user -> Tuples.of(session, user));
		}).map(tuple -> createDto(tuple.getT1(), tuple.getT2()));
	}
	
	@Override
	public Mono<SessionDto> renewSession(SessionDto session) {
		return sessionRepo.findById(session.getUuid())
			.flatMap(s -> {
				s.setExpiration(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));
				logger.info("Session renewed for user {}", session.getUsername());
				return sessionRepo.save(s);
			}).map(s -> updateDto(session, s));
	}
	
	@Override
	public Mono<Void> closeSession(SessionDto session) {
		return sessionRepo.deleteById(session.getUuid());
	}
	
	private Mono<Session> createSession(UserDto user) {
		Session session = new Session();
		session.setUsername(user.getUsername());
		session.setExpiration(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));
		logger.info("Session created for user {}", user.getUsername());
		return sessionRepo.save(session);
	}
	
	private static SessionDto createDto(Session session, UserDto user) {
		SessionDto dto = new SessionDto();
		dto.setUuid(session.getUuid());
		dto.setUsername(user.getUsername());
		dto.setAdmin(user.isAdmin());
		dto.setExpiration(session.getExpiration().toEpochMilli());
		return dto;
	}
	
	private static SessionDto updateDto(SessionDto dto, Session entity) {
		dto.setExpiration(entity.getExpiration().toEpochMilli());
		return dto;
	}
	
	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void removeExpiredSessions() {
		sessionRepo.deleteByExpirationLessThan(Instant.now()).subscribe();
	}

}
