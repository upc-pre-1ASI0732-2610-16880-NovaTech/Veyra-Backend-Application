package com.novaperutech.veyra.platform.activities.interfaces.rest.resources;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novaperutech.veyra.platform.activities.interfaces.rest.support.FlexibleLocalDateDeserializer;
import com.novaperutech.veyra.platform.activities.interfaces.rest.support.FlexibleLocalTimeDeserializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateActivityResource(
        @NotNull String name,
        @NotNull @Valid ActivityPeriodResource activityPeriod,
        @NotNull String area,
        @NotNull Long residentId,
        @NotNull Long attendantId
) {
    public record ActivityPeriodResource(
            @NotNull @JsonDeserialize(using = FlexibleLocalDateDeserializer.class) LocalDate activityDate,
            @NotNull @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class) LocalTime startTime,
            @NotNull @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class) LocalTime endTime
    ) {}

    public LocalDate activityDate() { return activityPeriod.activityDate(); }
    public LocalTime startTime()    { return activityPeriod.startTime(); }
    public LocalTime endTime()      { return activityPeriod.endTime(); }
}
