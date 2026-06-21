package com.novaperutech.veyra.platform.activities.interfaces.rest.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlexibleLocalDateDeserializer extends StdDeserializer<LocalDate> {

    public FlexibleLocalDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String raw = p.getText().trim();

        // Plain date: "2026-06-30"
        try {
            return LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {}

        // ISO datetime with Z or offset: "2026-06-30T00:00:00.000Z" / "2026-06-30T00:00:00"
        try {
            return LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
        } catch (DateTimeParseException ignored) {}

        // ISO date-time without millis: "2026-06-30T00:00:00.000"
        try {
            return LocalDate.parse(raw.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {}

        throw ctx.weirdStringException(raw, LocalDate.class,
                "Cannot parse date. Expected format: yyyy-MM-dd (received: \"" + raw + "\")");
    }
}
