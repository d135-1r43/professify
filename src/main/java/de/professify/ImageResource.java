package de.professify;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jboss.resteasy.reactive.RestForm;

import java.nio.file.Files;

@Path("/image")
public class ImageResource
{
	@Inject
	ImageGenService imageGenService;

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("image/png")
	public byte[] generateImage(@RestForm("file") FileUpload file) throws Exception
	{
		byte[] imageBytes = Files.readAllBytes(file.uploadedFile());
		String mimeType = file.contentType() != null ? file.contentType() : "image/jpeg";
		return imageGenService.generateImage(imageBytes, mimeType);
	}
}
