package com.example.book.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

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
import com.example.book.service.dto.BookSearchResponse;
import com.example.book.service.dto.PublisherDto;

import net.lecousin.reactive.data.relational.query.SelectQuery;
import net.lecousin.reactive.data.relational.query.criteria.Criteria;
import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import reactor.core.CorePublisher;
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
	public Mono<BookSearchResponse> searchBooks(BookSearchRequest searchRequest) {
		SelectQuery<Book> query = SelectQuery.from(Book.class, "book")
			.join("book", "authors", "author")
			.join("book", "publisher", "publisher");
		if (!StringUtils.isBlank(searchRequest.getBookTitle()))
			query = query.where(Criteria.property("book", "title").toUpperCase().like('%' + searchRequest.getBookTitle().toUpperCase() + '%'));
		if (searchRequest.getYearFrom() != null)
			query = query.where(Criteria.property("book", "year").greaterOrEqualTo(searchRequest.getYearFrom()));
		if (searchRequest.getYearTo() != null)
			query = query.where(Criteria.property("book", "year").lessOrEqualTo(searchRequest.getYearTo()));
		if (!StringUtils.isBlank(searchRequest.getAuthorName()))
			query = query.where(Criteria.property("author", "name").toUpperCase().like('%' + searchRequest.getAuthorName().toUpperCase() + '%'));
		if (!StringUtils.isBlank(searchRequest.getPublisherName()))
			query = query.where(Criteria.property("publisher", "name").toUpperCase().like('%' + searchRequest.getPublisherName().toUpperCase() + '%'));
		if (searchRequest.getOffset() != null && searchRequest.getLimit() != null)
			query = query.limit(searchRequest.getOffset(), searchRequest.getLimit());
		if (searchRequest.getOrderBy() != null) {
			int i = searchRequest.getOrderBy().indexOf('.');
			if (i > 0)
				query = query.orderBy(searchRequest.getOrderBy().substring(0, i), searchRequest.getOrderBy().substring(i + 1), searchRequest.isOrderAsc());
		}
		Mono<Optional<Long>> count;
		if (searchRequest.isCountTotal())
			count = query.executeCount(template.getLcClient()).map(Optional::of);
		else
			count = Mono.just(Optional.empty());
		SelectQuery<Book> q = query;
		return count.zipWhen(nb -> q.execute(template.getLcClient()).map(BookDto::fromEntity).collectList())
			.map(tuple -> {
				BookSearchResponse response = new BookSearchResponse();
				if (tuple.getT1().isPresent())
					response.setCount(tuple.getT1().get());
				response.setBooks(tuple.getT2());
				return response;
			});
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
	
	@Override
	public Mono<BookDto> getBook(long bookId) {
		return bookRepo.findByIdWithAuthorsAndPublisher(bookId).map(BookDto::fromEntity);
	}
	
	@Override
	public Flux<AuthorDto> getAuthors(String name) {
		SelectQuery<Author> q = SelectQuery.from(Author.class, "author");
		if (name != null && !name.isEmpty())
			q = q.where(Criteria.property("author", "name").toUpperCase().like('%' + name.toUpperCase() + '%'));
		q = q.orderBy("author", "name", true);
		return q.execute(authorRepo.getLcClient()).map(AuthorDto::fromEntity);
	}
	
	@Override
	public Flux<PublisherDto> getPublishers(String name) {
		SelectQuery<Publisher> q = SelectQuery.from(Publisher.class, "publisher");
		if (name != null && !name.isEmpty())
			q = q.where(Criteria.property("publisher", "name").toUpperCase().like('%' + name.toUpperCase() + '%'));
		q = q.orderBy("publisher", "name", true);
		return q.execute(publisherRepo.getLcClient()).map(PublisherDto::fromEntity);
	}
	
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Mono<BookDto> saveBook(BookDto dto) {
		Flux<Author> authors;
		if (dto.getAuthors() == null)
			authors = Flux.empty();
		else {
			List<Long> ids = new LinkedList<>();
			List<CorePublisher<Author>> items = new LinkedList<>();
			for (AuthorDto a : dto.getAuthors()) {
				if (a.getId() != null && a.getId() > 0)
					ids.add(a.getId());
				else
					items.add(authorRepo.save(a.toEntity(new Author())));
			}
			if (!ids.isEmpty())
				items.add(authorRepo.findAllById(ids));
			authors = Flux.concat(items);
		}
		
		Mono<Publisher> publisher;
		if (dto.getPublisher() == null)
			publisher = Mono.empty();
		else if (dto.getPublisher().getId() != null && dto.getPublisher().getId() > 0)
			publisher = publisherRepo.findById(dto.getPublisher().getId());
		else
			publisher = publisherRepo.save(dto.getPublisher().toEntity(new Publisher()));
		
		
		return Mono.zip(authors.collectList(), publisher.<Optional<Publisher>>map(Optional::of).switchIfEmpty(Mono.just(Optional.empty())))
		.flatMap(tuple -> {
			if (dto.getId() != null && dto.getId() > 0) {
				return bookRepo.findByIdWithAuthorsAndPublisher(dto.getId())
					.switchIfEmpty(Mono.error(new HttpServerErrorException(HttpStatus.NOT_FOUND, "Book not found: id " + dto.getId())))
					.flatMap(book -> {
						// update book
						dto.toEntity(book);
						// update publisher
						book.setPublisher(tuple.getT2().orElse(null));
						// update authors
						book.setAuthors(new HashSet<>(book.getAuthors()));
						for (Iterator<Author> it = book.getAuthors().iterator(); it.hasNext(); ) {
							Author existing = it.next();
							boolean found = false;
							for (Author a : tuple.getT1()) {
								if (a.getId().longValue() == existing.getId().longValue()) {
									found = true;
									break;
								}
							}
							if (!found)
								it.remove();
						}
						for (Author a : tuple.getT1()) {
							boolean found = false;
							for (Author existing : book.getAuthors())
								if (existing.getId().longValue() == a.getId().longValue()) {
									found = true;
									break;
								}
							if (!found)
								book.getAuthors().add(a);
						}
						// save
						return bookRepo.save(book);
					}).map(BookDto::fromEntity);
			}
			Book book = dto.toEntity(new Book());
			book.setAuthors(new HashSet<>(tuple.getT1()));
			book.setPublisher(tuple.getT2().orElse(null));
			return bookRepo.save(book).map(BookDto::fromEntity);
		});
	}
	
	@Override
	public Mono<Void> deleteBook(long bookId) {
		return bookRepo.deleteById(bookId);
	}

}
