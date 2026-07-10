package com.novaperutech.veyra.platform.nursing.domain.model.commands;

public record CreateAllergyCommand(Long residentId, String allergenName, String reaction, String severityLevel, String typeOfAllergy) {
    public CreateAllergyCommand {
        if (residentId == null || residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be a positive number");
        }
        if (allergenName == null || allergenName.isBlank()) {
            throw new IllegalArgumentException("Allergen name cannot be null or blank");
        }
        if (reaction == null || reaction.isBlank()) {
            throw new IllegalArgumentException("Reaction cannot be null or blank");
        }
        if (severityLevel == null || severityLevel.isBlank()) {
            throw new IllegalArgumentException("Severity level cannot be null or blank");
        }
        if (typeOfAllergy == null || typeOfAllergy.isBlank()) {
            throw new IllegalArgumentException("Type of allergy cannot be null or blank");
        }
    }
}
