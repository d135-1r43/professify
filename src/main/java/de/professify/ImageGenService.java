package de.professify;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class ImageGenService
{
	private static final Logger LOG = Logger.getLogger(ImageGenService.class);

	private static final String API_ENDPOINT = "aiplatform.googleapis.com";
	private static final String MODEL_ID = "gemini-3-pro-image";
	private static final Pattern DATA_PATTERN = Pattern.compile("\"data\":\\s*\"([^\"]+)\"");

	private static final int ERROR_BODY_LIMIT = 2000;

	private static final String PROMPT = """
		Transform my selfie into a crisp, executive-level professional headshot suitable for C-suite LinkedIn profiles. 
		The lighting should be dramatic yet professional with soft shadows. My posture is confident and extremely cool and attractive, 
		wearing a dark navy blazer. The background is a modern corporate office with floor-to-ceiling windows, slightly blurred.""";

	@ConfigProperty(name = "vertex.api.key")
	String apiKey;

	protected final HttpClient httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(30))
		.build();

	public byte[] generateImage(byte[] inputImage, String mimeType) throws Exception
	{
		String base64Image = Base64.getEncoder().encodeToString(inputImage);
		String requestBody = buildRequestBody(base64Image, mimeType);

		// NOTE: url carries the API key as a query parameter, so it must never be logged.
		String url = String.format(
			"https://%s/v1/publishers/google/models/%s:streamGenerateContent?key=%s",
			API_ENDPOINT, MODEL_ID, URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
		);

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.header("Content-Type", "application/json")
			.timeout(Duration.ofSeconds(120))
			.POST(HttpRequest.BodyPublishers.ofString(requestBody))
			.build();

		LOG.infof("Calling Vertex AI: model=%s, inputBytes=%d, mimeType=%s, requestBodyBytes=%d",
			MODEL_ID, inputImage.length, mimeType, requestBody.length());

		long startedAt = System.nanoTime();
		HttpResponse<String> response;
		try
		{
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		}
		catch (IOException | InterruptedException e)
		{
			LOG.errorf(e, "Vertex AI call failed after %d ms without a response", elapsedMs(startedAt));
			throw e;
		}

		long elapsedMs = elapsedMs(startedAt);
		String body = response.body();

		Matcher matcher = DATA_PATTERN.matcher(body);
		if (matcher.find())
		{
			String base64Data = matcher.group(1);
			byte[] image = Base64.getDecoder().decode(base64Data);
			LOG.infof("Image generated: status=%d, outputBytes=%d, took %d ms",
				response.statusCode(), image.length, elapsedMs);
			return image;
		}

		// Collapsed to one line so a failure stays greppable as a single log entry.
		String excerpt = body.substring(0, Math.min(body.length(), ERROR_BODY_LIMIT))
			.replaceAll("\\s+", " ");
		LOG.errorf("No image data in Vertex AI response: status=%d, bodyLength=%d, took %d ms. Body excerpt: %s",
			response.statusCode(), body.length(), elapsedMs, excerpt);

		throw new RuntimeException("No image data found in response (status " + response.statusCode() + "): " + excerpt);
	}

	private static long elapsedMs(long startedAt)
	{
		return (System.nanoTime() - startedAt) / 1_000_000;
	}

	private String buildRequestBody(String base64Image, String mimeType)
	{
		return """
			{
				"contents": [
					{
						"role": "user",
						"parts": [
							{
								"inlineData": {
									"mimeType": "%s",
									"data": "%s"
								}
							},
							{
								"text": "%s"
							}
						]
					}
				],
				"generationConfig": {
					"temperature": 1,
					"maxOutputTokens": 32768,
					"responseModalities": ["TEXT", "IMAGE"],
					"topP": 0.95,
					"imageConfig": {
						"aspectRatio": "1:1",
						"imageSize": "1K",
						"imageOutputOptions": {
							"mimeType": "image/png"
						},
						"personGeneration": "ALLOW_ALL"
					}
				},
				"safetySettings": [
					{"category": "HARM_CATEGORY_HATE_SPEECH", "threshold": "OFF"},
					{"category": "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold": "OFF"},
					{"category": "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold": "OFF"},
					{"category": "HARM_CATEGORY_HARASSMENT", "threshold": "OFF"}
				]
			}
			""".formatted(mimeType, base64Image, PROMPT);
	}
}
