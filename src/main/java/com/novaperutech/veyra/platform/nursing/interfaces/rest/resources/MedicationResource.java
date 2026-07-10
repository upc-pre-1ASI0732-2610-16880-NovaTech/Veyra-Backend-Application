package com.novaperutech.veyra.platform.nursing.interfaces.rest.resources;

import java.time.LocalDate;

public record MedicationResource(Long id, Long nursingHomeId, String name, String description, Integer amount, LocalDate expirationDate, String drugPresentation, String dosage, String lot) {
}
