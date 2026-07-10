package com.novaperutech.veyra.platform.nursing.domain.model.commands;

public record AdministerMedicationCommand(Long medicationId, Long residentId, Integer quantity) {
    public AdministerMedicationCommand {
        if (medicationId == null || medicationId <= 0) {
            throw new IllegalArgumentException("Medication ID must be a positive number");
        }
        if (residentId == null || residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be a positive number");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}
