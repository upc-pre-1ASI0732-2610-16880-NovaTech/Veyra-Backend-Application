package com.novaperutech.veyra.platform.nursing.unit;

import com.novaperutech.veyra.platform.nursing.application.internal.queryservices.ResidentQueryServiceImpl;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Resident;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetActiveResidentsByNursingHomeId;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetResidentByIdQuery;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.ResidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NursingServiceUnitTest {

    @Mock
    private ResidentRepository residentRepository;

    @InjectMocks
    private ResidentQueryServiceImpl residentQueryService;

    @Test
    void shouldReturnEntityWhenExists() {
        Resident resident = org.mockito.Mockito.mock(Resident.class);
        when(residentRepository.findById(1L)).thenReturn(Optional.of(resident));

        var result = residentQueryService.handle(new GetResidentByIdQuery(1L));

        assertTrue(result.isPresent());
        verify(residentRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenEntityDoesNotExist() {
        when(residentRepository.findById(99L)).thenReturn(Optional.empty());

        var result = residentQueryService.handle(new GetResidentByIdQuery(99L));

        assertTrue(result.isEmpty());
        verify(residentRepository).findById(99L);
    }

    @Test
    void shouldReturnEntitiesWhenExists() {
        var residents = List.of(org.mockito.Mockito.mock(Resident.class), org.mockito.Mockito.mock(Resident.class));
        when(residentRepository.findByNursingHomeIdAndResidentStatus(10L,
                com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.ResidentState.ACTIVE))
                .thenReturn(residents);

        var result = residentQueryService.handle(new GetActiveResidentsByNursingHomeId(10L));

        assertEquals(2, result.size());
        verify(residentRepository).findByNursingHomeIdAndResidentStatus(10L,
                com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.ResidentState.ACTIVE);
    }
}
