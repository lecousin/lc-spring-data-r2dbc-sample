package com.example.user.dao.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import net.lecousin.reactive.data.relational.annotations.ColumnDefinition;
import net.lecousin.reactive.data.relational.annotations.GeneratedValue;
import net.lecousin.reactive.data.relational.annotations.Index;

@Table("users")
@Index(name = "username_index", unique = true, properties = "username")
public class User {

	@Id @GeneratedValue
	private Long id;
	
	@Column
	@ColumnDefinition(nullable = false, max = 100)
	private String username;
	
	@Column
	@ColumnDefinition(nullable = false, max = 64)
	private String password;
	
	@Column
	@ColumnDefinition(nullable = false)
	private boolean admin;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
}
