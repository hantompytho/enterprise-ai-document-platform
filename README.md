# Enterprise AI Document Platform

A secure Spring Boot backend for document management with authentication, authorization, file processing, AI-ready summarization, and PostgreSQL persistence.

## Features

- User registration and login
- BCrypt password hashing
- JWT access token authentication
- Refresh tokens with rotation
- Logout support
- Role-based authorization
- USER and ADMIN roles
- Document ownership
- Secure document CRUD operations
- Multipart file upload
- File storage in PostgreSQL
- File download
- Text extraction with Apache Tika
- AI summary service abstraction
- Document search
- Pagination
- Global exception handling
- Request validation
- Swagger / OpenAPI documentation
- Unit tests
- Integration tests with Testcontainers
- GitHub Actions CI

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Docker
- Testcontainers
- Apache Tika
- JWT
- Maven
- Swagger / OpenAPI

## Architecture

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  +---- Authentication / Authorization
  |
  +---- Text Extraction
  |
  +---- AI Summary Service
  |
  v
Repository
  |
  v
PostgreSQL