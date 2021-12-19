package com.example.book.dao.repository;

import com.example.book.dao.model.Book;

import net.lecousin.reactive.data.relational.repository.LcR2dbcRepository;

public interface BookRepository extends LcR2dbcRepository<Book, Long> {

}
