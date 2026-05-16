package com.novaperutech.veyra.platform.shared.unit;

import com.novaperutech.veyra.platform.shared.infrastructure.persistence.jpa.configuration.strategy.SnakeCaseWithPluralizedTablePhysicalNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SharedNamingStrategyUnitTest {

    private final SnakeCaseWithPluralizedTablePhysicalNamingStrategy strategy =
            new SnakeCaseWithPluralizedTablePhysicalNamingStrategy();

    @Test
    void shouldCreateEntitySuccessfully() {
        Identifier identifier = Identifier.toIdentifier("PersonProfile");

        Identifier result = strategy.toPhysicalTableName(identifier, null);

        assertEquals("person_profiles", result.getText());
    }

    @Test
    void shouldReturnEmptyWhenEntityDoesNotExist() {
        Identifier result = strategy.toPhysicalColumnName(null, null);

        assertNull(result);
    }
}
