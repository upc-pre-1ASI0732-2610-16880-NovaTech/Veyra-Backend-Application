package com.novaperutech.veyra.platform.activities.interfaces.rest.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlexibleLocalTimeDeserializer extends StdDeserializer<LocalTime> {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    public FlexibleLocalTimeDeserializer() {
        super(LocalTime.class);
    }

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String raw = p.getText().trim();

        // "HH:mm:ss" or "HH:mm:ss.SSS"
        try {
            return LocalTime.parse(raw, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException ignored) {}

        // "HH:mm" (no seconds)
        try {
            return LocalTime.parse(raw, HH_MM);
        } catch (DateTimeParseException ignored) {}

        throw ctx.weirdStringException(raw, LocalTime.class,
                "Cannot parse time. Expected format: HH:mm or HH:mm:ss (received: \"" + raw + "\")");
    }
}
