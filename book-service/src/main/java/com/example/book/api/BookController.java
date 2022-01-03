package com.example.book.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.service.BookService;
import com.example.book.service.dto.AuthorDto;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;
import com.example.book.service.dto.BookSearchResponse;
import com.example.book.service.dto.PublisherDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/book/v1")
public class BookController {

	@Autowired
	private BookService bookService;
	
	@GetMapping("/book/{bookId}")
	public Mono<BookDto> getBook(@PathVariable("bookId") long bookId) {
		return bookService.getBook(bookId);
	}
	
	@PostMapping("/book")
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<BookDto> saveBook(@RequestBody BookDto book) {
		return bookService.saveBook(book);
	}
	
	@DeleteMapping("/book/{bookId}")
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<Void> deleteBook(@PathVariable("bookId") long bookId) {
		return bookService.deleteBook(bookId);
	}
	
	@PostMapping("/search/book")
	public Mono<BookSearchResponse> searchBooks(@RequestBody BookSearchRequest request) {
		return bookService.searchBooks(request);
	}
	
	@GetMapping("/search/author")
	public Flux<AuthorDto> searchAuthors(@RequestParam("name") String authorName) {
		return bookService.getAuthors(authorName);
	}
	
	@GetMapping("/search/publisher")
	public Flux<PublisherDto> searchPublishers(@RequestParam("name") String publisherName) {
		return bookService.getPublishers(publisherName);
	}

}
