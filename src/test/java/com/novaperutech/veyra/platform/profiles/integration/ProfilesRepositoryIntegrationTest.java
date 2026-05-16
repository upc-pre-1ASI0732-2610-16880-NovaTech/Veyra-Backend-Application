package com.novaperutech.veyra.platform.profiles.integration;

import com.novaperutech.veyra.platform.profiles.domain.model.aggregates.PersonProfile;
import com.novaperutech.veyra.platform.profiles.domain.model.valueobjects.Dni;
import com.novaperutech.veyra.platform.profiles.infrastructure.persistence.jpa.repositories.PersonProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ProfilesRepositoryIntegrationTest {

    @Autowired
    private PersonProfileRepository personProfileRepository;

    @MockBean(name = "mongoMappingContext")
    private MongoMappingContext mongoMappingContext;

    @Test
    void shouldCreateEntitySuccessfully() {
        var savedProfile = personProfileRepository.save(newPersonProfile("12345678"));

        assertTrue(savedProfile.getId() != null);
        assertEquals("12345678", savedProfile.getDni().dni());
    }

    @Test
    void shouldReturnEntityWhenExists() {
        var savedProfile = personProfileRepository.save(newPersonProfile("87654321"));

        var result = personProfileRepository.findByDni(new Dni("87654321"));

        assertTrue(result.isPresent());
        assertEquals(savedProfile.getId(), result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenEntityDoesNotExist() {
        var result = personProfileRepository.findByDni(new Dni("55667788"));

        assertTrue(result.isEmpty());
        assertFalse(personProfileRepository.existsByDni(new Dni("55667788")));
    }

    private PersonProfile newPersonProfile(String dni) {
        return new PersonProfile(
                dni,
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
                "https://cdn.test/photo.jpg",
                "profiles/photo-1",
                "987654321"
        );
    }
}
