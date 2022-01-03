package com.example.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.service.UserService;
import com.example.user.service.dto.NewUserRequest;
import com.example.user.service.dto.UserDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user/v1")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public Flux<UserDto> listUsers() {
		return userService.getUsers();
	}
	
	@GetMapping("/user/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<UserDto> getUser(@PathVariable("userId") long userId) {
		return userService.getUser(userId);
	}
	
	@GetMapping("/me")
	public Mono<UserDto> getMyUser(Authentication auth) {
		return userService.getUser(((UserDetails)auth.getPrincipal()).getUsername());
	}
	
	@GetMapping("/user/{userId}/resetpassword")
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<String> resetPassword(@PathVariable("userId") long userId) {
		return userService.resetPassword(userId);
	}
	
	@PutMapping("/user/{userId}/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<UserDto> setAdministrator(@PathVariable("userId") long userId, @RequestParam("admin") boolean admin) {
		return userService.setAdministrator(userId, admin);
	}
	
	@PutMapping("/me/password")
	public Mono<UserDto> changeMyPassword(@RequestBody String newPassword, Authentication auth) {
		return userService.changePassword(((UserDetails)auth.getPrincipal()).getUsername(), newPassword);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<UserDto> createUser(@RequestBody NewUserRequest newUser) {
		return userService.createUser(newUser.getUsername(), newUser.getPassword(), newUser.isAdmin());
	}

}
