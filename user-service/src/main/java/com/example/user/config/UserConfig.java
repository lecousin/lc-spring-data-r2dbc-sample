package com.example.user.config;

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
@EnableR2dbcRepositories(repositoryFactoryBeanClass = LcR2dbcRepositoryFactoryBean.class, basePackages = "com.example.user.dao.repository", entityOperationsRef = "userOperations")
public class UserConfig extends LcR2dbcEntityOperationsBuilder {

	@Bean
	@Qualifier("userDatabaseConnectionFactory")
	public ConnectionFactory userDatabaseConnectionFactory(@Value("${database.user}") String databaseUrl) {
		return ConnectionFactories.get(databaseUrl);
	}
	
	@Bean
	@Qualifier("userOperations")
	public LcR2dbcEntityTemplate userOperations(@Qualifier("userDatabaseConnectionFactory") ConnectionFactory connectionFactory) {
		return buildEntityOperations(connectionFactory);
	}

}
