package de.professify;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import static java.util.Objects.requireNonNull;

@Path("/")
public class UploadPage
{
	private final Template upload;

	public UploadPage(Template upload)
	{
		this.upload = requireNonNull(upload, "upload is required");
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance get()
	{
		return upload.instance();
	}
}
