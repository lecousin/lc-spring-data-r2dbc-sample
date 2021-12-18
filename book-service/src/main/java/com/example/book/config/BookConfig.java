package com.example.book.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import net.lecousin.reactive.data.relational.configuration.LcR2dbcEntityOperationsBuilder;
import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import net.lecousin.reactive.data.relational.repository.LcR2dbcRepositoryFactoryBean;

@Configuration
@EnableR2dbcRepositories(repositoryFactoryBeanClass = LcR2dbcRepositoryFactoryBean.class, basePackages = "com.example.book.dao.repository", entityOperationsRef = "bookOperations")
public class BookConfig extends LcR2dbcEntityOperationsBuilder {

	@Bean
	@Qualifier("bookDatabaseConnectionFactory")
	public ConnectionFactory bookDatabaseConnectionFactory(@Value("${database.book}") String databaseUrl) {
		return ConnectionFactories.get(databaseUrl);
	}
	
	@Bean
	@Qualifier("bookOperations")
	public LcR2dbcEntityTemplate bookOperations(@Qualifier("bookDatabaseConnectionFactory") ConnectionFactory connectionFactory) {
		return buildEntityOperations(connectionFactory);
	}

}
