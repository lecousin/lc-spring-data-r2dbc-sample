package com.example.app;

import java.io.IOException;
import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.auth.api.TokenProvider;

import net.lecousin.reactive.data.relational.LcReactiveDataRelationalInitializer;
import reactor.core.publisher.Mono;

@SpringBootApplication
@ComponentScan("com.example")
@EnableWebFluxSecurity
public class ExampleApplication {

	public static void main(String[] args) {
		LcReactiveDataRelationalInitializer.init();
		SpringApplication.run(ExampleApplication.class, args);
	}
	
	@Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http, TokenProvider tokenFilter) {
		return http
			.httpBasic().disable()
			.exceptionHandling().authenticationEntryPoint((exchange, exception) -> {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return Mono.empty();
			}).and()
			.csrf().disable()
			.authorizeExchange()
				.pathMatchers("/", "/*.html", "/*.js", "/*.css").permitAll()
				.pathMatchers("/api/auth/v1", "/api/initdb").permitAll()
				.pathMatchers("/api/**").authenticated()
				.and()
			.securityContextRepository(tokenFilter)
			.build()
			;
	}
	
	@Bean
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        eventMulticaster.setTaskExecutor(executor);
        return eventMulticaster;
    }
	
	@Bean
	public RouterFunction<ServerResponse> router() {
		return RouterFunctions.route(request -> "/".equals(request.path()), request -> ServerResponse.temporaryRedirect(URI.create("/index.html")).build())
			.and(RouterFunctions.route(request -> request.path().matches("^/[^/]+\\.(html|js|css)$"), request -> {
				ClassPathResource resource = new ClassPathResource("web-app" + request.path());
				if (!resource.exists())
					return ServerResponse.notFound().build();
				try {
					return ServerResponse.ok().contentLength(resource.contentLength()).contentType(getContentType(request.path())).body(BodyInserters.fromValue(resource));
				} catch (IOException e) {
					return ServerResponse.notFound().build();
				}
			}));
	}
	
	private static MediaType getContentType(String filename) {
		int i = filename.lastIndexOf('.');
		if (i < 0)
			return MediaType.APPLICATION_OCTET_STREAM;
		String ext = filename.substring(i + 1).toLowerCase();
		switch (ext) {
		case "html": return MediaType.TEXT_HTML;
		case "js": return new MediaType("text", "javascript");
		case "css": return new MediaType("text", "css");
		default: return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

}
