package com.novaperutech.veyra.platform.health.unit;

import com.novaperutech.veyra.platform.health.application.internal.queryservices.AllergyQueryServiceImpl;
import com.novaperutech.veyra.platform.health.domain.model.aggregates.Allergy;
import com.novaperutech.veyra.platform.health.domain.model.queries.ExistsAllergyByResidentIdQuery;
import com.novaperutech.veyra.platform.health.domain.model.queries.GetAllergiesByResidentIdQuery;
import com.novaperutech.veyra.platform.health.domain.model.valueobjects.ResidentId;
import com.novaperutech.veyra.platform.health.infrastructure.persistence.jpa.repositories.AllergyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthServiceUnitTest {

    @Mock
    private AllergyRepository allergyRepository;

    @InjectMocks
    private AllergyQueryServiceImpl allergyQueryService;

    @Test
    void shouldReturnEntityWhenExists() {
        var residentId = new ResidentId(5L);
        var allergies = List.of(org.mockito.Mockito.mock(Allergy.class));
        when(allergyRepository.findAllByResidentId(residentId)).thenReturn(allergies);

        var result = allergyQueryService.handle(new GetAllergiesByResidentIdQuery(residentId));

        assertEquals(1, result.size());
        verify(allergyRepository).findAllByResidentId(residentId);
    }

    @Test
    void shouldReturnFalseWhenEntityDoesNotExist() {
        var residentId = new ResidentId(8L);
        when(allergyRepository.existsByResidentId(residentId)).thenReturn(false);

        var result = allergyQueryService.handle(new ExistsAllergyByResidentIdQuery(residentId));

        assertFalse(result);
        verify(allergyRepository).existsByResidentId(residentId);
    }
}
