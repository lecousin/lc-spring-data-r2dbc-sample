package com.example.book.service.dto;

import java.util.LinkedList;
import java.util.List;

import com.example.book.dao.model.Book;

public class BookDto {

	private Long id;
	private String name;
	private Integer year;
	private List<Author> authors;
	private Publisher publisher;
	
	public static class Author {
		private Long id;
		private String name;

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
		
	}
	
	public static class Publisher {
		private Long id;
		private String name;

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

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
	
	public static BookDto fromEntity(Book entity) {
		BookDto dto = new BookDto();
		dto.setId(entity.getId());
		dto.setName(entity.getTitle());
		dto.setYear(entity.getYear());
		if (entity.getAuthors() != null) {
			dto.setAuthors(new LinkedList<>());
			for (com.example.book.dao.model.Author a : entity.getAuthors()) {
				Author adto = new Author();
				adto.setId(a.getId());
				adto.setName(a.getName());
				dto.getAuthors().add(adto);
			}
		}
		if (entity.getPublisher() != null) {
			Publisher p = new Publisher();
			p.setId(entity.getPublisher().getId());
			p.setName(entity.getPublisher().getName());
			dto.setPublisher(p);
		}
		return dto;
	}
}
