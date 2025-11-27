package de.professify;

import dev.langchain4j.data.image.Image;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.net.URI;

@Path("/endpoint")
public class ImageGenerator
{
	@Inject
	ImageGenService imageGenerationAiService;

	@GET
	@Path("/generate-image")
	@Produces("image/png")
	public byte[] generateImage()
	{
		Image image = imageGenerationAiService.generateImage();
		return readBytes(image.url());
	}

	private byte[] readBytes(URI url)
	{
		try (var is = url.toURL().openStream())
		{
			return is.readAllBytes();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to read image from URL: " + url, e);
		}
	}
}