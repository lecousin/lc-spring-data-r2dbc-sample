package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.auth.api.TokenProvider;

import net.lecousin.reactive.data.relational.LcReactiveDataRelationalInitializer;

@SpringBootApplication
@ComponentScan("com.example")
@EnableWebFluxSecurity
public class ExampleApplication {

	public static void main(String[] args) {
		LcReactiveDataRelationalInitializer.init();
		SpringApplication.run(ExampleApplication.class, args);
	}
	
	@Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http, TokenProvider tokenFilter) {
		return http
			.httpBasic().disable()
			.csrf().disable()
			.authorizeExchange()
				.pathMatchers("/api/auth/v1/login").permitAll()
				.pathMatchers("/api/**").authenticated()
			.and()
			.securityContextRepository(tokenFilter)
			.build()
			;
	}
}
