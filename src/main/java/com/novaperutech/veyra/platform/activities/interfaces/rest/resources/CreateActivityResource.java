package com.novaperutech.veyra.platform.activities.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novaperutech.veyra.platform.activities.interfaces.rest.support.FlexibleLocalDateDeserializer;
import com.novaperutech.veyra.platform.activities.interfaces.rest.support.FlexibleLocalTimeDeserializer;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateActivityResource(
        @NotNull String name,
        @NotNull @JsonDeserialize(using = FlexibleLocalDateDeserializer.class) LocalDate activityDate,
        @NotNull @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class) LocalTime startTime,
        @NotNull @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class) LocalTime endTime,
        @NotNull String area,
        @NotNull Long residentId,
        @NotNull Long attendantId
) {}
