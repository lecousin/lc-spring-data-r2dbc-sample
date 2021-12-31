package com.example.book.dao.repository;

import com.example.book.dao.model.Book;

import net.lecousin.reactive.data.relational.query.SelectQuery;
import net.lecousin.reactive.data.relational.query.criteria.Criteria;
import net.lecousin.reactive.data.relational.repository.LcR2dbcRepository;
import reactor.core.publisher.Mono;

public interface BookRepository extends LcR2dbcRepository<Book, Long> {

	default SelectQuery<Book> selectWithAuthorsAndPublisher() {
		return SelectQuery.from(Book.class, "book")
			.join("book", "authors", "author")
			.join("book", "publisher", "publisher");
	}
	
	default Mono<Book> findByIdWithAuthorsAndPublisher(long bookId) {
		return selectWithAuthorsAndPublisher().where(Criteria.property("book", "id").is(bookId)).execute(getLcClient()).next();
	}
	
}
