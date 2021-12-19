package com.example.book.service.dto;

import java.util.List;

import com.example.book.dao.model.Book;

public class BookDto {

	private Long id;
	private String title;
	private Integer year;
	private List<AuthorDto> authors;
	private PublisherDto publisher;
	
	public BookDto() {
	}
	
	public BookDto(String title, Integer year) {
		this.title = title;
		this.year = year;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<AuthorDto> getAuthors() {
		return authors;
	}

	public void setAuthors(List<AuthorDto> authors) {
		this.authors = authors;
	}

	public PublisherDto getPublisher() {
		return publisher;
	}

	public void setPublisher(PublisherDto publisher) {
		this.publisher = publisher;
	}
	
	public static BookDto fromEntity(Book entity) {
		BookDto dto = new BookDto();
		dto.setId(entity.getId());
		dto.setTitle(entity.getTitle());
		dto.setYear(entity.getYear());
		dto.setAuthors(AuthorDto.fromEntities(entity.getAuthors()));
		dto.setPublisher(PublisherDto.fromEntity(entity.getPublisher()));
		return dto;
	}
	
	public Book toEntity(Book entity) {
		entity.setTitle(getTitle());
		entity.setYear(getYear());
		return entity;
	}
}
