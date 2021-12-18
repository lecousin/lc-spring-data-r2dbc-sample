package com.example.book.dao.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import net.lecousin.reactive.data.relational.annotations.ColumnDefinition;
import net.lecousin.reactive.data.relational.annotations.ForeignTable;
import net.lecousin.reactive.data.relational.annotations.GeneratedValue;

@Table
public class Publisher {

	@Id @GeneratedValue
	private Long id;
	
	@Column
	@ColumnDefinition(nullable = false, max = 100)
	private String name;
	
	@ForeignTable(joinKey = "publisher", optional = true)
	private Set<Book> books;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Book> getBooks() {
		return books;
	}

	public void setBooks(Set<Book> books) {
		this.books = books;
	}

	public Long getId() {
		return id;
	}
	
}
