package com.novaperutech.veyra.platform.nursing.domain.exceptions;

/**
 * Exception thrown when a medication already exists for a nursing home.
 * <p>
 * This exception is thrown when attempting to create a medication
 * with the same name and lot that already exists in a specific nursing home's inventory.
 * </p>
 * @see RuntimeException
 */
public class MedicationAlreadyExistsException extends RuntimeException {
    /**
     * Constructor for the exception.
     * @param medicationName The name of the medication that already exists.
     * @param lot The lot of the medication that already exists.
     * @param nursingHomeId The ID of the nursing home.
     */
    public MedicationAlreadyExistsException(String medicationName, String lot, Long nursingHomeId) {
        super(String.format("Medication '%s' with lot '%s' already exists in nursing home with ID %s.",
                medicationName, lot, nursingHomeId));
    }
}