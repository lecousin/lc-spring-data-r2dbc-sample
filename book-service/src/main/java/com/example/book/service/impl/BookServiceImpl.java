package com.example.book.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.book.dao.model.Author;
import com.example.book.dao.model.Book;
import com.example.book.dao.model.Publisher;
import com.example.book.dao.repository.AuthorRepository;
import com.example.book.dao.repository.BookRepository;
import com.example.book.dao.repository.PublisherRepository;
import com.example.book.service.BookService;
import com.example.book.service.dto.AuthorDto;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;
import com.example.book.service.dto.PublisherDto;

import net.lecousin.reactive.data.relational.query.SelectQuery;
import net.lecousin.reactive.data.relational.query.criteria.Criteria;
import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import net.lecousin.reactive.data.relational.schema.RelationalDatabaseSchema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookServiceImpl implements BookService {
	
	@Autowired
	@Qualifier("bookOperations")
	private LcR2dbcEntityTemplate template;
	
	@Autowired
	private BookRepository bookRepo;
	
	@Autowired
	private AuthorRepository authorRepo;
	
	@Autowired
	private PublisherRepository publisherRepo;

	@Override
	public Mono<Void> initDatabase() {
		RelationalDatabaseSchema schema = template.getLcClient().buildSchemaFromEntities(Arrays.asList(Book.class, Author.class, Publisher.class));
		return template.getLcClient().dropCreateSchemaContent(schema)
		.then(
			Mono.zip(
				createAuthor(new AuthorDto("John Smith")),
				createAuthor(new AuthorDto("William Miller")),
				createPublisher(new PublisherDto("My Editor")),
				createPublisher(new PublisherDto("Other Editor"))
			).flatMap(tuple -> {
				BookDto book1 = new BookDto("My first book", 1970);
				book1.setAuthors(Arrays.asList(tuple.getT1()));
				book1.setPublisher(tuple.getT3());
				BookDto book2 = new BookDto("Second one", null);
				book2.setAuthors(Arrays.asList(tuple.getT1(), tuple.getT2()));
				BookDto book3 = new BookDto("Another", 2011);
				book3.setAuthors(Arrays.asList(tuple.getT2()));
				book3.setPublisher(tuple.getT4());
				return Mono.when(createBook(book1), createBook(book2), createBook(book3));
			})
		);
	}
	
	@Override
	public Flux<BookDto> searchBooks(BookSearchRequest searchRequest) {
		SelectQuery<Book> query = SelectQuery.from(Book.class, "book")
			.join("book", "authors", "author")
			.join("book", "publisher", "publisher");
		if (!StringUtils.isBlank(searchRequest.getBookTitle()))
			query = query.where(Criteria.property("book", "title").like('%' + searchRequest.getBookTitle() + '%'));
		if (searchRequest.getYearFrom() != null)
			query = query.where(Criteria.property("book", "year").greaterOrEqualTo(searchRequest.getYearFrom()));
		if (searchRequest.getYearTo() != null)
			query = query.where(Criteria.property("book", "year").lessOrEqualTo(searchRequest.getYearTo()));
		if (!StringUtils.isBlank(searchRequest.getAuthorName()))
			query = query.where(Criteria.property("author", "name").like('%' + searchRequest.getAuthorName() + '%'));
		if (!StringUtils.isBlank(searchRequest.getPublisherName()))
			query = query.where(Criteria.property("publisher", "name").like('%' + searchRequest.getPublisherName() + '%'));
		return query.execute(template.getLcClient()).map(BookDto::fromEntity);
	}
	
	@Override
	public Mono<AuthorDto> createAuthor(AuthorDto author) {
		return Mono.just(author).map(dto -> dto.toEntity(new Author())).flatMap(authorRepo::save).map(AuthorDto::fromEntity);
	}
	
	@Override
	public Mono<PublisherDto> createPublisher(PublisherDto publisher) {
		return Mono.just(publisher).map(dto -> dto.toEntity(new Publisher())).flatMap(publisherRepo::save).map(PublisherDto::fromEntity);
	}
	
	@Override
	public Mono<BookDto> createBook(BookDto book) {
		Flux<Author> authors;
		if (book.getAuthors() == null)
			authors = Flux.empty();
		else
			authors = authorRepo.findAllById(book.getAuthors().stream().map(author -> author.getId()).collect(Collectors.toList()));
		Mono<Optional<Publisher>> publisher;
		if (book.getPublisher() == null)
			publisher = Mono.just(Optional.empty());
		else
			publisher = publisherRepo.findById(book.getPublisher().getId()).map(Optional::of).switchIfEmpty(Mono.just(Optional.empty()));
		return Mono.zip(authors.collectList(), publisher)
		.flatMap(tuple -> {
			Book b = book.toEntity(new Book());
			b.setAuthors(new HashSet<>(tuple.getT1()));
			if (tuple.getT2().isPresent())
				b.setPublisher(tuple.getT2().get());
			return bookRepo.save(b);
		}).map(BookDto::fromEntity);
	}

}
