package com.example.user.service.dto;

import com.example.user.dao.model.User;

public class UserDto {

	private Long id;
	private String username;
	private boolean admin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public static UserDto fromEntity(User entity) {
		if (entity == null)
			return null;
		UserDto dto = new UserDto();
		dto.setId(entity.getId());
		dto.setUsername(entity.getUsername());
		dto.setAdmin(entity.isAdmin());
		return dto;
	}
	
}
