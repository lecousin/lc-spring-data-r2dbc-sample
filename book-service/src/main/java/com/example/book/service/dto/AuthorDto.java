package com.example.book.service.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.book.dao.model.Author;

public class AuthorDto {

	private Long id;
	private String name;
	
	public AuthorDto() {
	}
	
	public AuthorDto(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static AuthorDto fromEntity(Author entity) {
		if (entity == null)
			return null;
		AuthorDto dto = new AuthorDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		return dto;
	}
	
	public static List<AuthorDto> fromEntities(Collection<Author> entities) {
		if (entities == null)
			return null;
		ArrayList<AuthorDto> dtos = new ArrayList<>(entities.size());
		for (Author entity : entities)
			dtos.add(fromEntity(entity));
		return dtos;
	}
	
	public Author toEntity(Author entity) {
		entity.setName(getName());
		return entity;
	}
}
