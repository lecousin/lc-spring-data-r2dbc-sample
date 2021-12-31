package com.example.book.dao.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import net.lecousin.reactive.data.relational.annotations.ColumnDefinition;
import net.lecousin.reactive.data.relational.annotations.ForeignKey;
import net.lecousin.reactive.data.relational.annotations.ForeignKey.OnForeignDeleted;
import net.lecousin.reactive.data.relational.annotations.GeneratedValue;
import net.lecousin.reactive.data.relational.annotations.JoinTable;

@Table
public class Book {

	@Id @GeneratedValue
	private Long id;
	
	@Column
	@ColumnDefinition(nullable = false, max = 100)
	private String title;
	
	@Column
	@ColumnDefinition(nullable = true)
	private Integer year;
	
	@JoinTable(joinProperty = "books", columnName = "book")
	private Set<Author> authors;
	
	@ForeignKey(optional = true, cascadeDelete = false, onForeignDeleted = OnForeignDeleted.SET_TO_NULL)
	private Publisher publisher;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public Long getId() {
		return id;
	}
	
}
