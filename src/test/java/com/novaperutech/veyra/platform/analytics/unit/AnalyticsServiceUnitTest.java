package com.novaperutech.veyra.platform.analytics.unit;

import com.novaperutech.veyra.platform.analytics.application.internal.queryservices.MetricQueryServiceImpl;
import com.novaperutech.veyra.platform.analytics.domain.model.aggregates.Metric;
import com.novaperutech.veyra.platform.analytics.domain.model.queries.GetResidentAdmissionsByNursingHomeIdAndYearQuery;
import com.novaperutech.veyra.platform.analytics.domain.model.valueobjects.MetricType;
import com.novaperutech.veyra.platform.analytics.domain.model.valueobjects.NursingHomeId;
import com.novaperutech.veyra.platform.analytics.infrastructure.persistence.jpa.repositories.MetricRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceUnitTest {

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private MetricQueryServiceImpl metricQueryService;

    @Test
    void shouldReturnEntityWhenExists() {
        var nursingHomeId = new NursingHomeId(1L);
        var query = new GetResidentAdmissionsByNursingHomeIdAndYearQuery(nursingHomeId, 2025);
        var metrics = List.of(org.mockito.Mockito.mock(Metric.class));

        when(metricRepository.findByNursingHomeIdAndMetricTypeAndYear(nursingHomeId, MetricType.RESIDENT_ADMISSION, 2025))
                .thenReturn(metrics);

        var result = metricQueryService.handle(query);

        assertEquals(1, result.size());
        verify(metricRepository).findByNursingHomeIdAndMetricTypeAndYear(nursingHomeId, MetricType.RESIDENT_ADMISSION, 2025);
    }

    @Test
    void shouldRejectInvalidRequest() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new GetResidentAdmissionsByNursingHomeIdAndYearQuery(new NursingHomeId(1L), 2026));

        assertEquals("year must be between 1900 and 2025", exception.getMessage());
    }
}
