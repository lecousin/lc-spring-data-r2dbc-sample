package com.example.book.service;

import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;

import reactor.core.publisher.Flux;

public interface BookService {

	Flux<BookDto> searchBooks(BookSearchRequest searchRequest);
	
}
