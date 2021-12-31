package com.example.book.service;

import com.example.book.service.dto.AuthorDto;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;
import com.example.book.service.dto.BookSearchResponse;
import com.example.book.service.dto.PublisherDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookService {
	
	Mono<BookDto> createBook(BookDto book);
	
	Mono<BookDto> getBook(long bookId);

	Mono<BookSearchResponse> searchBooks(BookSearchRequest searchRequest);
	
	Flux<AuthorDto> getAuthors(String name);
	
	Mono<AuthorDto> createAuthor(AuthorDto author);
	
	Flux<PublisherDto> getPublishers(String name);
	
	Mono<PublisherDto> createPublisher(PublisherDto publisher);
	
	Mono<BookDto> saveBook(BookDto book);
	
	Mono<Void> deleteBook(long bookId);
	
}
