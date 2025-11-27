package de.professify;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
@SystemMessage("You are an AI that generates images from text prompts.")
public interface ImageGenService
{
	@UserMessage("Generate a linkedin profile pic of a male person")
	Image generateImage();
}