# Enterprise AI Document Platform

Enterprise-style Spring Boot backend application for document management, REST APIs, PostgreSQL persistence and future AI-powered document summaries.

## Project Goal

This project demonstrates a modern backend architecture using Java, Spring Boot, PostgreSQL, Docker and clean REST API design.

The goal is to build a realistic business application similar to internal enterprise tools used for document management, automation and AI-assisted workflows.

## Current Features

- Health check endpoint
- Document CRUD API
- PostgreSQL database persistence
- Docker Compose setup for local database
- DTO-based request and response models
- Validation using Jakarta Bean Validation
- Global exception handling
- Clean layered architecture

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Maven
- Jakarta Validation

## Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
