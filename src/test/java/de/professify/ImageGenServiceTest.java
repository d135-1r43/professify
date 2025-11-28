package de.professify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class ImageGenServiceTest
{
	@Test
	public void testGenerateImageSuccessfully() throws Exception
	{
		// Arrange
		ImageGenService imageGenService = new ImageGenService();
		byte[] inputImage = "test-image-data".getBytes(StandardCharsets.UTF_8);
		String mimeType = "image/jpeg";
		String apiKey = "dummy-api-key";

		// Mocking the API response
		String base64Output = Base64.getEncoder().encodeToString("output-image-data".getBytes(StandardCharsets.UTF_8));
		String mockResponseBody = "{\"data\": \"" + base64Output + "\"}";
		HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
		Mockito.when(mockResponse.body()).thenReturn(mockResponseBody);

		HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
		Mockito.when(mockHttpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
			.thenReturn(mockResponse);

		// Inject mock dependencies
		imageGenService.apiKey = apiKey;
		imageGenService.httpClient = mockHttpClient;

		// Act
		byte[] result = imageGenService.generateImage(inputImage, mimeType);

		// Assert
		Assertions.assertNotNull(result);
		Assertions.assertEquals("output-image-data", new String(result, StandardCharsets.UTF_8));
	}

	@Test
	public void testGenerateImageNoDataInResponse() throws Exception
	{
		// Arrange
		ImageGenService imageGenService = new ImageGenService();
		byte[] inputImage = "test-image-data".getBytes(StandardCharsets.UTF_8);
		String mimeType = "image/jpeg";
		String apiKey = "dummy-api-key";

		// Mocking the API response
		String mockResponseBody = "{}"; // No data field present
		HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
		Mockito.when(mockResponse.body()).thenReturn(mockResponseBody);

		HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
		Mockito.when(mockHttpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
			.thenReturn(mockResponse);

		// Inject mock dependencies
		imageGenService.apiKey = apiKey;
		imageGenService.httpClient = mockHttpClient;

		// Act & Assert
		RuntimeException exception = Assertions.assertThrows(
			RuntimeException.class,
			() -> imageGenService.generateImage(inputImage, mimeType)
		);
		Assertions.assertTrue(exception.getMessage().startsWith("No image data found in response"));
	}

	@Test
	public void testGenerateImageInvalidBase64Data() throws Exception
	{
		// Arrange
		ImageGenService imageGenService = new ImageGenService();
		byte[] inputImage = "test-image-data".getBytes(StandardCharsets.UTF_8);
		String mimeType = "image/jpeg";
		String apiKey = "dummy-api-key";

		// Mocking the API response
		String mockResponseBody = "{\"data\": \"invalid-base64-data\"}";
		HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
		Mockito.when(mockResponse.body()).thenReturn(mockResponseBody);

		HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
		Mockito.when(mockHttpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
			.thenReturn(mockResponse);

		// Inject mock dependencies
		imageGenService.apiKey = apiKey;
		imageGenService.httpClient = mockHttpClient;

		// Act & Assert
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> imageGenService.generateImage(inputImage, mimeType)
		);
	}
}