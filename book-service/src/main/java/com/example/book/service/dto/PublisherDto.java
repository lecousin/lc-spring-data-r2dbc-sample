package com.example.book.service.dto;

import com.example.book.dao.model.Publisher;

public class PublisherDto {

	private Long id;
	private String name;
	
	public PublisherDto() {
	}
	
	public PublisherDto(String name) {
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

	public static PublisherDto fromEntity(Publisher entity) {
		if (entity == null)
			return null;
		PublisherDto dto = new PublisherDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		return dto;
	}
	
	public Publisher toEntity(Publisher entity) {
		entity.setName(getName());
		return entity;
	}
}
