package com.example.book.service.impl;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.book.dao.model.Author;
import com.example.book.dao.model.Book;
import com.example.book.dao.model.Publisher;
import com.example.book.service.BookService;
import com.example.book.service.dto.BookDto;
import com.example.book.service.dto.BookSearchRequest;

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
	
	@PostConstruct
	public void initDatabase() {
		if (System.getProperty("createDatabase") != null) {
			RelationalDatabaseSchema schema = template.getLcClient().buildSchemaFromEntities(Arrays.asList(Book.class, Author.class, Publisher.class));
			template.getLcClient().createSchemaContent(schema)
			.then(Mono.defer(() -> {
				Author a1 = new Author();
				a1.setName("John Smith");
				Author a2 = new Author();
				a2.setName("William Miller");
				Publisher p1 = new Publisher();
				p1.setName("My Editor");
				Publisher p2 = new Publisher();
				p2.setName("Other Editor");
				Book b1 = new Book();
				b1.setTitle("My first book");
				b1.setAuthors(new HashSet<>(Arrays.asList(a1)));
				b1.setPublisher(p1);
				Book b2 = new Book();
				b2.setTitle("Second one");
				b2.setAuthors(new HashSet<>(Arrays.asList(a1, a2)));
				Book b3 = new Book();
				b3.setTitle("Another");
				b3.setAuthors(new HashSet<>(Arrays.asList(a2)));
				b3.setPublisher(p2);
				return template.getLcClient().saveAll(a1, a2, p1, p2, b1, b2, b3);
			}))
			.subscribe();
		}
	}
	
	@Override
	public Flux<BookDto> searchBooks(BookSearchRequest searchRequest) {
		SelectQuery<Book> query = SelectQuery.from(Book.class, "book")
			.join("book", "authors", "author")
			.join("book", "publisher", "publisher");
		if (!StringUtils.isBlank(searchRequest.getBookName()))
			query = query.where(Criteria.property("book", "title").like('%' + searchRequest.getBookName() + '%'));
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

}
