package com.example.book.service.dto;

import java.util.List;

public class BookSearchResponse {

	private Long count;
	private List<BookDto> books;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<BookDto> getBooks() {
		return books;
	}

	public void setBooks(List<BookDto> books) {
		this.books = books;
	}

}
