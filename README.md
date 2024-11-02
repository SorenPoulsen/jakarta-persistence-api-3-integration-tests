# Java Persistence API 3 integration tests

Project to demonstrate how to set up Java Persistence API 3 integration tests with Hibernate, Apache Derby and Maven's 
Failsafe plugin.

Maven's Failsafe plugin runs all tests named *IT.java. The tests create a simple JPA entity manager in standalone mode, 
without an application server or a JTA transaction context.

The JPA entities schemas are created in the Derby in-memory Database on every test execution, by setting the property 
**hibernate.hbm2ddl.auto** to "create-drop" in JPA's [src/test/resources/META-INF/persistence.xml](./src/test/resources/META-INF/persistence.xml).

Hibernate executes the SQL statements in [src/test/resources/import.sql](./src/test/resources/import.sql), to load data 
into the tables, just after it has generated the database schema.

A fuller description of the set up is found at [https://sorenpoulsen.com/jakarta-persistence-3-integration-tests](https://sorenpoulsen.com/jakarta-persistence-3-integration-tests). 

## Requirements

- JDK 11
- Maven 3.8.6+

## Build and run integration tests

    $ mvn verify