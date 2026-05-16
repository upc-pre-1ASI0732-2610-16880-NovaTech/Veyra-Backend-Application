package com.novaperutech.veyra.platform.tracking.unit;

import com.novaperutech.veyra.platform.tracking.application.internal.queryservices.DeviceQueryServiceImpl;
import com.novaperutech.veyra.platform.tracking.domain.model.aggregates.Device;
import com.novaperutech.veyra.platform.tracking.domain.model.queries.GetDeviceByIdQuery;
import com.novaperutech.veyra.platform.tracking.domain.model.queries.GetUnassignedDevicesQuery;
import com.novaperutech.veyra.platform.tracking.domain.model.valueobjects.AssignmentStatus;
import com.novaperutech.veyra.platform.tracking.infrastructure.persistence.jpa.repositories.DeviceRepository;
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
class TrackingServiceUnitTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceQueryServiceImpl deviceQueryService;

    @Test
    void shouldReturnEntityWhenExists() {
        Device device = org.mockito.Mockito.mock(Device.class);
        when(deviceRepository.findByDeviceId("DEV-001")).thenReturn(Optional.of(device));

        var result = deviceQueryService.handle(new GetDeviceByIdQuery("DEV-001"));

        assertTrue(result.isPresent());
        verify(deviceRepository).findByDeviceId("DEV-001");
    }

    @Test
    void shouldReturnEmptyWhenEntityDoesNotExist() {
        when(deviceRepository.findByDeviceId("DEV-404")).thenReturn(Optional.empty());

        var result = deviceQueryService.handle(new GetDeviceByIdQuery("DEV-404"));

        assertTrue(result.isEmpty());
        verify(deviceRepository).findByDeviceId("DEV-404");
    }

    @Test
    void shouldReturnEntitiesWhenExists() {
        var devices = List.of(org.mockito.Mockito.mock(Device.class), org.mockito.Mockito.mock(Device.class));
        when(deviceRepository.findAllByStatus(AssignmentStatus.UNASSIGNED)).thenReturn(devices);

        var result = deviceQueryService.handle(new GetUnassignedDevicesQuery());

        assertEquals(2, result.size());
        verify(deviceRepository).findAllByStatus(AssignmentStatus.UNASSIGNED);
    }
}
