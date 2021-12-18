package com.example.auth.dao.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import net.lecousin.reactive.data.relational.annotations.ColumnDefinition;
import net.lecousin.reactive.data.relational.annotations.GeneratedValue;

@Table("sessions")
public class Session {

	@Id @GeneratedValue(strategy = GeneratedValue.Strategy.RANDOM_UUID)
	private UUID uuid;
	
	@Column
	@ColumnDefinition(nullable = false, max = 100)
	private String username;
	
	@Column
	private Instant expiration;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Instant getExpiration() {
		return expiration;
	}

	public void setExpiration(Instant expiration) {
		this.expiration = expiration;
	}

	public UUID getUuid() {
		return uuid;
	}
	
}
