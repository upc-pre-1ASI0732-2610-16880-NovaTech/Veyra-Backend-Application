package com.novaperutech.veyra.platform.nursing.interfaces.rest.resources;

public record MedicationAlertResource(Long medicationId, String medicationName, String alertType, String message) {
}
