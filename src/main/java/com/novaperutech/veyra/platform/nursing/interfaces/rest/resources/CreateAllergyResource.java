package com.novaperutech.veyra.platform.nursing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record CreateAllergyResource(
        @NotBlank(message = "Allergen name is required") String allergenName,
        @NotBlank(message = "Reaction is required") String reaction,
        @NotBlank(message = "Severity level is required") String severityLevel,
        @NotBlank(message = "Type of allergy is required") String typeOfAllergy
) {
}
