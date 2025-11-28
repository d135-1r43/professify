# Professify

AI-powered professional headshot generator. Upload a selfie and get a polished, executive-level LinkedIn profile photo.

## Features

- Transform casual selfies into professional headshots
- Uses Google Vertex AI (Gemini) for image generation
- Web-based upload interface
- REST API for programmatic access

## Prerequisites

- Java 21
- A Google Cloud account with Vertex AI API enabled
- Vertex AI API key

## Configuration

Set your Vertex AI API key in `application.properties`:

```properties
vertex.api.key=your-api-key-here
```

## Running the Application

### Development Mode

```bash
./mvnw quarkus:dev
```

The application will be available at http://localhost:8080. Dev UI is at http://localhost:8080/q/dev/.

### Production

```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

### Native Executable

```bash
./mvnw package -Dnative
./target/professify-1.0.0-SNAPSHOT-runner
```

## Usage

### Web Interface

Navigate to http://localhost:8080 and upload your selfie through the web form.

### REST API

```bash
curl -X POST http://localhost:8080/image \
  -F "file=@your-selfie.jpg" \
  --output professional-headshot.png
```

## Tech Stack

- Quarkus 3.x (Java 21)
- Google Vertex AI (Gemini 3 Pro Image)
- Qute templating
- Jakarta REST

---

**Disclaimer:** This is a non-commercial tech demo project for educational purposes only.

[View on GitHub](https://github.com/mhe/professify)
