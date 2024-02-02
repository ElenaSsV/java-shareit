# ShareIt Project

## Description :memo::
Backend for item sharing app. The idea is that if someone needs an item for a short time and doesn't want to buy it, he can borrow it using the app. 

Project has multi-module architecture. It consists of 2 micro-services:

- gateway - responsible for receiving and validation of users requests;
- server which contains all the logics and supports the following operations:

***Items***:
- Posting new item;
- Editing item by its owner;
- Retrieving info on certain item by owner;
- Retrieving info on all items by owner;
- Searching items by text in item name and description;
  
***Bookings***:
- Booking item for a certain period;
- Retrieving info on certaing booking by item owner or booker;
- Retrieving info on all bookings by booker and state (ALL, PAST, FUTURE, WAITING);
- Retrieving info on all bookings by item owner and state (ALL, PAST, FUTURE, WAITING);
  
***Item requests***:
- Adding new item request if item is not found through search;
- Retrieving info on all requests and replies to them by requester, sorted from newest to oldest;
- Getting info on all requests by any user, sorted from newest to oldest;
- Retrieve info on certain request and replies to it by any user;

## Stack :hammer::
Java 11, Spring Boot, Spring Data, Hibernate, PostgreSQL, Docker, Maven, Lombok, JUnit, Mockito, Postman

## Instructions to deploy:
Requirements: JDK 11, Docker, Maven

1. Clone the [repository](https://github.com/ElenaSsV/java-shareit) to your computer;
2. mvn clean package;
3. docker-compose up


