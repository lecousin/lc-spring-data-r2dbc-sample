package com.example.auth.api;

import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.auth.service.AuthService;
import com.example.auth.service.dto.SessionDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;


@Component
public class TokenProvider implements ServerSecurityContextRepository {
	
	@Autowired
	private AuthService authService;
	
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
    	return Mono.defer(() -> {
        	List<String> auths = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        	if (auths == null)
        		return Mono.empty();
        	String bearerToken = null;
        	for (String s : auths)
        		if (s.startsWith("Bearer ")) {
        			bearerToken = s.substring(7, s.length());
        			break;
        		}
        	if (bearerToken == null)
        		return Mono.empty();
        	return Mono.just(bearerToken);
    	})
    	.map(this::parseToken)
    	.flatMap(session -> authService.checkSession(session.getUuid(), session.getUsername()))
    	.map(session -> new UsernamePasswordAuthenticationToken(session, null, session.isAdmin() ? Arrays.asList(new SimpleGrantedAuthority("ADMIN")) : new LinkedList<>()))
   		.map(SecurityContextImpl::new);
    }

	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		return Mono.empty();
	}
	
	public String generateToken(SessionDto session) {
		try {
			return Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(session));
		} catch (Exception e) {
			throw new RuntimeException("Unable to generate token", e);
		}
	}
	
	public SessionDto parseToken(String token) {
		try {
			return new ObjectMapper().readValue(Base64.getDecoder().decode(token), SessionDto.class);
		} catch (Exception e) {
			throw new RuntimeException("Invalid token", e);
		}
	}
}