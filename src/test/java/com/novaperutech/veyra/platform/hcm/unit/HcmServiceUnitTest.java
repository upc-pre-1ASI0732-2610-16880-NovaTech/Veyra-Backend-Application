package com.novaperutech.veyra.platform.hcm.unit;

import com.novaperutech.veyra.platform.hcm.application.internal.queryservices.StaffQueryServiceImpl;
import com.novaperutech.veyra.platform.hcm.domain.model.aggregates.Staff;
import com.novaperutech.veyra.platform.hcm.domain.model.queries.GetActiveContractByStaffMemberIdQuery;
import com.novaperutech.veyra.platform.hcm.domain.model.queries.GetStaffByIdQuery;
import com.novaperutech.veyra.platform.hcm.infrastructure.persistence.jpa.repositories.StaffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HcmServiceUnitTest {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffQueryServiceImpl staffQueryService;

    @Test
    void shouldReturnEntityWhenExists() {
        Staff staff = org.mockito.Mockito.mock(Staff.class);
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

        var result = staffQueryService.handle(new GetStaffByIdQuery(1L));

        assertTrue(result.isPresent());
        verify(staffRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEntityDoesNotExist() {
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class,
                () -> staffQueryService.handle(new GetActiveContractByStaffMemberIdQuery(99L)));

        assertEquals("Staff with id 99 does not exist", exception.getMessage());
        verify(staffRepository).findById(99L);
    }
}
