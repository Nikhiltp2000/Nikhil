# Multi Module Spring boot project

## About

This project is a multi-module Spring Boot application that allows users to perform GET and POST operations and interact with a MySQL database. The application uses JPA and Hibernate for data persistence and includes modules for controllers, models, repositories, and services. It also incorporates logging using the Log4j2 framework.  

## Table of Contents

- [Modules](#modules)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Build and Run](#build-and-run)
- [Usage](#usage)
- [Logging](#logging)


## Modules 

  The project is organised into the following modules:

 - `controller`: Contains the REST API controllers responsible for handling incoming requests and mapping them to appropriate methods.
 - `model`: Defines the data models or entities used by the application.
 - `repository`: Provides interfaces for data access and communication with the MySQL database using JPA and Hibernate.
 - `service`: Implements the business logic and handles the interactions between controllers and repositories.


## Prerequisites

Before running the application, ensure that you have the following prerequisites installed:

 - Java Development Kit (JDK) 17
 - Maven
 - MySQL database server
 - IDE : intellij community edition

## Configuration

To configure the application:

1. Open the `application.properties` file located in the `src/main/resources` directory.

2. Update the database connection details such as `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` according to your MySQL configuration.

## Build and Run 

To build and run the application:

 1. open command prompt.
 2. Navigate to the root directory of the project.
 3. Run the `mvn clean install` command to build the application. 
 4. Once the build is successful, start the application using : `mvn spring-boot:run` command.

## Usage

 - To perform the GET request and retrieve data, use : `http://localhost:8080/getdata`
 - To perform the POST request and add data to the database, use : `http://localhost:8080/createuser` with the payload in json format.
 - User can use tools like postman or cURL to hit the api with the required payload.

## Deployment 

This project has been deployed on AWS. To access the deployed application, use the following endpoint:

 - GET : `ec2-15-207-89-139.ap-south-1.compute.amazonaws.com:8080/getdata`
 - POST : `ec2-15-207-89-139.ap-south-1.compute.amazonaws.com:8080/createuser`

## Logging

This project also uses Log4j2 framework for logging. The configuration for the logging can be found in the `log4j2.xml` file located in the `src/main/resources` directory.

   