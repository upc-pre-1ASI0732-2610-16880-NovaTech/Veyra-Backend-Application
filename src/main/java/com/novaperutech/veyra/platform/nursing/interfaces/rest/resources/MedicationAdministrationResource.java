package com.novaperutech.veyra.platform.nursing.interfaces.rest.resources;

import java.time.LocalDateTime;

public record MedicationAdministrationResource(Long id, Long medicationId, String medicationName, Long residentId, Integer quantity, LocalDateTime administeredAt) {
}
