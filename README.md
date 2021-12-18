# lc-spring-data-r2dbc sample

This project illustrate the usage of [lc-spring-data-r2dbc](https://github.com/lecousin/lc-spring-data-r2dbc) with multiple databases into a single Spring Boot application.

It is composed of the following modules:
 - [user-service](https://github.com/lecousin/lc-spring-data-r2dbc-sample/tree/master/user-service) provides a list of user with encrypted password, connected to a PostgreSQL database
 - [auth-service](https://github.com/lecousin/lc-spring-data-r2dbc-sample/tree/master/auth-service) provides security with a login REST method and uses an in memory H2 database to store sessions
 - [book-service](https://github.com/lecousin/lc-spring-data-r2dbc-sample/tree/master/book-service) provides a list of books with linked authors and publisher, stored in a MySQL database, and a REST method to search books
 - [app](https://github.com/lecousin/lc-spring-data-r2dbc-sample/tree/master/app) is the Spring Boot application startup containing all other modules and the database configuration
 
The databases are configured in [application.yaml](https://github.com/lecousin/lc-spring-data-r2dbc-sample/tree/master/app/src/main/resources/application.yaml), and changing this configuration allows to easily switch from one database to another.

Each module is configuring its own database connection, based on the application.yaml file, by extending class `LcR2dbcEntityOperationsBuilder` like in [BookConfig](https://github.com/lecousin/lc-spring-data-r2dbc-sample/tree/master/book-service/src/main/java/com/example/book/config/BookConfig.java)

To run the application, you can modify the application.yaml to provide your own database connection URL, then run the `ExampleApplication` class. To automatically create the tables on the first run, you can add the `createDatabase` property by launching the application with `-DcreateDatabase`.

To test the application, you can use a REST client:
 - login: POST http://localhost:8080/api/auth/v1/login with the body `{"username": "test", "password": "test"}` then get the returned token
 - search books: POST http://localhost:8080/api/book/v1/search with header `Authorization: Bearer <token>` (with the token returned by the login) and body `{"bookName": "o"}`
