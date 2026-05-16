package com.novaperutech.veyra.platform.tracking.unit;

import org.junit.jupiter.api.Test;
import org.springframework.util.ClassUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TrackingServiceUnitTest {

    @Test
    void shouldNotExposeTrackingBoundedContextInThisBranch() {
        assertFalse(
                ClassUtils.isPresent(
                        "com.novaperutech.veyra.platform.tracking.application.internal.queryservices.DeviceQueryServiceImpl",
                        getClass().getClassLoader()
                )
        );
    }
}
