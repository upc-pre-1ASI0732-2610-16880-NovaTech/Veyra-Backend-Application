package com.novaperutech.veyra.platform.profiles.unit;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.novaperutech.veyra.platform.profiles.infrastructure.storage.cloudinary.services.CloudinaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilesStorageUnitTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    private CloudinaryServiceImpl cloudinaryService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
        cloudinaryService = new CloudinaryServiceImpl(cloudinary);
    }

    @Test
    void shouldUploadImageSuccessfully() throws Exception {
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of(
                        "secure_url", "https://res.cloudinary.com/test/image/upload/v1/photo.png",
                        "public_id", "profiles/photo_123"));

        var result = cloudinaryService.upload(new byte[]{1, 2, 3}, "photo.png");

        assertEquals("https://res.cloudinary.com/test/image/upload/v1/photo.png", result.get("url"));
        assertEquals("profiles/photo_123", result.get("publicId"));
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void shouldHandleCloudinaryUploadError() throws Exception {
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("upload failed"));

        var exception = assertThrows(RuntimeException.class,
                () -> cloudinaryService.upload(new byte[]{1, 2, 3}, "photo.png"));

        assertTrue(exception.getMessage().contains("Error uploading file to Cloudinary"));
        verify(uploader).upload(any(byte[].class), anyMap());
    }
}
