package com.example.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import net.lecousin.reactive.data.relational.configuration.LcR2dbcEntityOperationsBuilder;
import net.lecousin.reactive.data.relational.repository.LcR2dbcEntityTemplate;
import net.lecousin.reactive.data.relational.repository.LcR2dbcRepositoryFactoryBean;

@Configuration
@EnableR2dbcRepositories(repositoryFactoryBeanClass = LcR2dbcRepositoryFactoryBean.class, basePackages = "com.example.auth.dao.repository", entityOperationsRef = "authOperations")
public class AuthConfig extends LcR2dbcEntityOperationsBuilder {

	@Bean
	@Qualifier("authDatabaseConnectionFactory")
	public ConnectionFactory authDatabaseConnectionFactory() {
		return ConnectionFactories.get("r2dbc:h2:mem:///sessions;DB_CLOSE_DELAY=-1;");
	}
	
	@Bean
	@Qualifier("authOperations")
	public LcR2dbcEntityTemplate authOperations(@Qualifier("authDatabaseConnectionFactory") ConnectionFactory connectionFactory) {
		return buildEntityOperations(connectionFactory);
	}

}
