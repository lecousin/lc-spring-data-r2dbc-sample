package com.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.service.BookService;
import com.example.user.service.UserService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/initdb")
public class InitDatabaseController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private BookService bookService;
	
	@GetMapping
	public Mono<String> initDb() {
		return Mono.when(userService.initDatabase(), bookService.initDatabase()).thenReturn("Database initialized");
	}

}
