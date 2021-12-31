package com.example.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.service.BookDatabaseInitializer;
import com.example.user.service.UserDatabaseInitializer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/initdb")
public class InitDatabaseController {

	@Autowired
	private UserDatabaseInitializer userService;
	
	@Autowired
	private BookDatabaseInitializer bookService;
	
	@GetMapping(produces = "application/json")
	public Mono<List<String>> initDb() {
		return userService.initSchema()
			.then(Mono.defer(() -> bookService.initSchema()))
			.then(Mono.just(Arrays.asList("users", "authors", "publishers", "books")));
	}
	
	@GetMapping("/{element}")
	public Mono<Void> initElement(@PathVariable("element") String element) {
		switch (element) {
		case "users": return userService.createUsers();
		case "authors": return bookService.createAuthors();
		case "publishers": return bookService.createPublishers();
		case "books": return bookService.createBooks();
		default: return Mono.error(new IllegalArgumentException("Unknown element: " + element));
		}
	}

}
