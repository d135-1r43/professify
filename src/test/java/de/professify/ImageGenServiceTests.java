package de.professify;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@QuarkusTest
@Tag("integration")
class ImageGenServiceTests
{
	@Inject
	ImageGenService imageGenService;

	@Test
	void shouldGenerateImage() throws Exception
	{
		// given
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("selfie.jpg"))
		{
			assert is != null;
			byte[] selfieByteArray = is.readAllBytes();

			// when
			byte[] generated = imageGenService.generateImage(selfieByteArray, "image/jpg");

			// then
			// Check if it starts with PNG signature: 89 50 4E 47 0D 0A 1A 0A
			byte[] pngSignature = new byte[] { (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
			byte[] actualHeader = Arrays.copyOfRange(generated, 0, 8);
			assertArrayEquals(pngSignature, actualHeader, "The byte array does not start with the PNG signature");
		}
	}
}
