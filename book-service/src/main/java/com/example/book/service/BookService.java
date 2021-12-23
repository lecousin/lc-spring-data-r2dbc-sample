package com.example.book.service;

import com.example.book.service.dto.AuthorDto;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;
import com.example.book.service.dto.BookSearchResponse;
import com.example.book.service.dto.PublisherDto;

import reactor.core.publisher.Mono;

public interface BookService {

	Mono<BookSearchResponse> searchBooks(BookSearchRequest searchRequest);
	
	Mono<AuthorDto> createAuthor(AuthorDto author);
	
	Mono<PublisherDto> createPublisher(PublisherDto publisher);
	
	Mono<BookDto> createBook(BookDto book);
	
	Mono<Void> initDatabase();
	
}
