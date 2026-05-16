package com.novaperutech.veyra.platform.activities.unit;

import com.novaperutech.veyra.platform.activities.application.internal.commandservices.ActivityCommandServiceImpl;
import com.novaperutech.veyra.platform.activities.application.internal.outboundservices.acl.ActivityExternalServices;
import com.novaperutech.veyra.platform.activities.domain.model.aggregates.Activity;
import com.novaperutech.veyra.platform.activities.domain.model.commands.CreateActivityCommand;
import com.novaperutech.veyra.platform.activities.infrastructure.persistence.jpa.repositories.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivitiesServiceUnitTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityExternalServices externalServices;

    @InjectMocks
    private ActivityCommandServiceImpl activityCommandService;

    @Test
    void shouldCreateEntitySuccessfully() {
        when(externalServices.residentExists(10L)).thenReturn(true);
        when(externalServices.staffExists(20L)).thenReturn(true);
        when(activityRepository.save(any(Activity.class))).thenAnswer(invocation -> {
            Activity activity = invocation.getArgument(0);
            ReflectionTestUtils.setField(activity, "id", 99L);
            return activity;
        });

        Long activityId = activityCommandService.handle(validCreateCommand());

        assertEquals(99L, activityId);
        verify(externalServices).residentExists(10L);
        verify(externalServices).staffExists(20L);
        verify(activityRepository).save(any(Activity.class));
    }

    @Test
    void shouldThrowExceptionWhenEntityDoesNotExist() {
        when(externalServices.residentExists(10L)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> activityCommandService.handle(validCreateCommand()));

        assertEquals("Resident with ID 10 does not exist.", exception.getMessage());
        verify(externalServices).residentExists(10L);
        verify(externalServices, never()).staffExists(20L);
        verify(activityRepository, never()).save(any(Activity.class));
    }

    private CreateActivityCommand validCreateCommand() {
        return new CreateActivityCommand(
                "Board Games",
                LocalDate.of(2025, 5, 10),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                "COMMON_AREA",
                1L,
                10L,
                20L
        );
    }
}
