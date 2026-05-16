package com.novaperutech.veyra.platform.profiles.unit;

import com.novaperutech.veyra.platform.profiles.application.internal.commandservices.PersonProfileCommandServiceImpl;
import com.novaperutech.veyra.platform.profiles.application.internal.outboundservices.storage.StorageService;
import com.novaperutech.veyra.platform.profiles.domain.exceptions.PersonProfileNotFoundException;
import com.novaperutech.veyra.platform.profiles.domain.model.aggregates.PersonProfile;
import com.novaperutech.veyra.platform.profiles.domain.model.commands.CreatePersonProfileCommand;
import com.novaperutech.veyra.platform.profiles.domain.model.commands.UpdatePersonProfileCommand;
import com.novaperutech.veyra.platform.profiles.infrastructure.persistence.jpa.repositories.PersonProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilesServiceUnitTest {

    @Mock
    private PersonProfileRepository personProfileRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private PersonProfileCommandServiceImpl personProfileCommandService;

    @Test
    void shouldCreateEntitySuccessfully() {
        when(storageService.upload(any(byte[].class), any(String.class)))
                .thenReturn(Map.of("url", "https://cdn.test/photo.jpg", "publicId", "profiles/photo-1"));

        var result = personProfileCommandService.handle(createCommand());

        assertTrue(result.isPresent());
        assertEquals("12345678", result.get().getDni().dni());
        assertEquals("https://cdn.test/photo.jpg", result.get().getPhoto().photoUrl());
        verify(storageService).upload(any(byte[].class), any(String.class));
        verify(personProfileRepository).save(any(PersonProfile.class));
    }

    @Test
    void shouldThrowExceptionWhenEntityDoesNotExist() {
        when(personProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PersonProfileNotFoundException.class,
                () -> personProfileCommandService.handle(updateCommand(99L)));

        verify(personProfileRepository).findById(99L);
        verify(storageService, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    void shouldRejectInvalidRequest() {
        when(storageService.upload(any(byte[].class), any(String.class)))
                .thenThrow(new RuntimeException("storage unavailable"));

        var exception = assertThrows(IllegalArgumentException.class,
                () -> personProfileCommandService.handle(createCommand()));

        assertEquals("Error uploading photo to storage: storage unavailable", exception.getMessage());
        verify(storageService).upload(any(byte[].class), any(String.class));
        verify(personProfileRepository, never()).save(any(PersonProfile.class));
    }

    private CreatePersonProfileCommand createCommand() {
        return new CreatePersonProfileCommand(
                "12345678",
                "Jane",
                "Doe",
                LocalDate.of(1990, 1, 1),
                34,
                "jane.doe@example.com",
                "Main Street",
                "123",
                "Lima",
                "15001",
                "Peru",
                new byte[]{1, 2, 3},
                "profile.png",
                "987654321"
        );
    }

    private UpdatePersonProfileCommand updateCommand(Long profileId) {
        return new UpdatePersonProfileCommand(
                profileId,
                "12345678",
                "Jane",
                "Doe",
                LocalDate.of(1990, 1, 1),
                34,
                "jane.doe@example.com",
                "Main Street",
                "123",
                "Lima",
                "15001",
                "Peru",
                null,
                null,
                "987654321"
        );
    }
}
