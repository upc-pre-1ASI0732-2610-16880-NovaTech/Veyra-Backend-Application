package com.novaperutech.veyra.platform.nursing.domain.model.commands;

import java.time.LocalDate;

public record CreateMedicationCommand(String name, String description, Integer amount, LocalDate expirationDate,String drugPresentation,String dosage, String lot, Long nursingHomeId) {
    public CreateMedicationCommand{
        if (nursingHomeId == null || nursingHomeId <= 0) {
            throw new IllegalArgumentException("Nursing home ID must be a positive number");
        }

        if (lot == null || lot.isBlank()) {
            throw new IllegalArgumentException("Lot cannot be null or blank");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Medication name cannot be null or blank");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }

        if (expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }
        if (drugPresentation == null || drugPresentation.isBlank()) {
            throw new IllegalArgumentException("Drug presentation cannot be null or blank");
        }

        if (dosage == null || dosage.isBlank()) {
            throw new IllegalArgumentException("Dosage cannot be null or blank");
        }

    }
}
