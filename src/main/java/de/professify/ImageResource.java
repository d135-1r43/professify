package de.professify;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jboss.resteasy.reactive.RestForm;

import java.nio.file.Files;

@Path("/image")
public class ImageResource
{
	private static final Logger LOG = Logger.getLogger(ImageResource.class);

	@Inject
	ImageGenService imageGenService;

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("image/png")
	public byte[] generateImage(@RestForm("file") FileUpload file) throws Exception
	{
		byte[] imageBytes = Files.readAllBytes(file.uploadedFile());
		String mimeType = file.contentType() != null ? file.contentType() : "image/jpeg";

		// fileName is client-supplied; sanitized so it cannot forge extra log lines.
		LOG.infof("POST /image: fileName=%s, sizeBytes=%d, contentType=%s",
			sanitize(file.fileName()), imageBytes.length, sanitize(file.contentType()));

		long startedAt = System.nanoTime();
		try
		{
			byte[] result = imageGenService.generateImage(imageBytes, mimeType);
			LOG.infof("POST /image succeeded in %d ms", (System.nanoTime() - startedAt) / 1_000_000);
			return result;
		}
		catch (Exception e)
		{
			// No throwable here on purpose: the service logs the cause and Quarkus'
			// error handler logs the stack trace, so this only adds the timing.
			LOG.errorf("POST /image failed after %d ms: %s: %s",
				(System.nanoTime() - startedAt) / 1_000_000, e.getClass().getSimpleName(), abbreviate(e.getMessage()));
			throw e;
		}
	}

	private static String sanitize(String value)
	{
		return value == null ? "<none>" : value.replaceAll("[\\r\\n]", "_");
	}

	private static String abbreviate(String message)
	{
		if (message == null)
		{
			return "<no message>";
		}
		String collapsed = message.replaceAll("\\s+", " ");
		return collapsed.length() <= 200 ? collapsed : collapsed.substring(0, 200) + "...";
	}
}
