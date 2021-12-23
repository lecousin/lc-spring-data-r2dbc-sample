package com.example.book.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.service.BookService;
import com.example.book.service.dto.BookSearchRequest;
import com.example.book.service.dto.BookSearchResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/book/v1")
public class BookController {

	@Autowired
	private BookService bookService;
	
	@PostMapping("/search")
	public Mono<BookSearchResponse> searchBooks(@RequestBody BookSearchRequest request) {
		return bookService.searchBooks(request);
	}

}
