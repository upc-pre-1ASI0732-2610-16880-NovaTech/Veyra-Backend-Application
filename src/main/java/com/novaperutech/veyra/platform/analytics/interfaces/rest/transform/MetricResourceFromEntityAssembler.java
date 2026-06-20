package com.novaperutech.veyra.platform.analytics.interfaces.rest.transform;

import com.novaperutech.veyra.platform.analytics.domain.model.aggregates.Metric;
import com.novaperutech.veyra.platform.analytics.interfaces.rest.resources.MetricResource;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricResourceFromEntityAssembler {

    private static final List<Month> ALL_MONTHS = List.of(Month.values());

    public static MetricResource toResourceFromEntityList(List<Metric> metrics) {
        return toResourceFromEntityList(metrics, null);
    }

    public static MetricResource toResourceFromEntityList(List<Metric> metrics, String metricTypeFallback) {
        Map<Month, Long> monthlyAggregation = metrics.stream()
                .collect(Collectors.groupingBy(
                        metric -> metric.getEventDate().getMonth(),
                        Collectors.summingLong(Metric::getValue)
                ));

        List<String> labels = ALL_MONTHS.stream()
                .map(month -> month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .toList();

        List<Long> values = ALL_MONTHS.stream()
                .map(month -> monthlyAggregation.getOrDefault(month, 0L))
                .toList();

        long total = values.stream().mapToLong(Long::longValue).sum();

        String metricType = metrics.isEmpty()
                ? (metricTypeFallback != null ? metricTypeFallback : "")
                : metrics.getFirst().getMetricType().name();

        return new MetricResource(labels, values, metricType, total);
    }
}