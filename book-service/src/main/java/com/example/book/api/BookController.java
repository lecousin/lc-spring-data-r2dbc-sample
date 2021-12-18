package com.example.book.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.service.BookService;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/book/v1")
public class BookController {

	@Autowired
	private BookService bookService;
	
	@PostMapping("/search")
	public Flux<BookDto> searchBooks(@RequestBody BookSearchRequest request) {
		return bookService.searchBooks(request);
	}

}
