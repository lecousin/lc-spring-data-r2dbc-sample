package com.example.book.service;

import com.example.book.service.dto.AuthorDto;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;
import com.example.book.service.dto.PublisherDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookService {

	Flux<BookDto> searchBooks(BookSearchRequest searchRequest);
	
	Mono<AuthorDto> createAuthor(AuthorDto author);
	
	Mono<PublisherDto> createPublisher(PublisherDto publisher);
	
	Mono<BookDto> createBook(BookDto book);
	
	Mono<Void> initDatabase();
	
}
