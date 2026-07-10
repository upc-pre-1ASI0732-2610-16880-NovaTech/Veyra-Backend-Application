package com.novaperutech.veyra.platform.nursing.interfaces.rest.resources;

public record AllergyResource(Long id, Long residentId, String allergenName, String reaction, String severityLevel, String typeOfAllergy) {
}
