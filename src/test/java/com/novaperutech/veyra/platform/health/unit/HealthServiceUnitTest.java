package com.novaperutech.veyra.platform.health.unit;

import org.junit.jupiter.api.Test;
import org.springframework.util.ClassUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;

class HealthServiceUnitTest {

    @Test
    void shouldNotExposeHealthBoundedContextInThisBranch() {
        assertFalse(
                ClassUtils.isPresent(
                        "com.novaperutech.veyra.platform.health.application.internal.queryservices.AllergyQueryServiceImpl",
                        getClass().getClassLoader()
                )
        );
    }
}
