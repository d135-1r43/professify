# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Run in dev mode with live reload
./mvnw quarkus:dev

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Package the application
./mvnw package

# Build native executable (requires GraalVM)
./mvnw package -Dnative
```

Dev UI available at http://localhost:8080/q/dev/ when running in dev mode.

## Architecture

This is a Quarkus application (Java 21) that provides AI-powered image generation using LangChain4j with OpenAI.

### Key Components

- **ImageGenService** (`src/main/java/de/professify/ImageGenService.java`): LangChain4j AI service interface using `@RegisterAiService`. Defines the AI interaction contract for image generation with system/user message annotations.

- **ImageGenerator** (`src/main/java/de/professify/ImageGenerator.java`): REST endpoint at `/endpoint` that exposes the image generation functionality. Handles HTTP requests and delegates to the AI service.

- **SomePage** (`src/main/java/de/professify/SomePage.java`): Example Qute template-based page at `/some-page`.

### Tech Stack

- Quarkus 3.30.x with REST (Jakarta REST)
- LangChain4j OpenAI integration for AI/image generation
- Qute for HTML templating
- Jackson for JSON serialization

### Configuration

OpenAI API key must be configured. The timeout is set to 80s in `application.properties`.
- Do not add the project tree to the README