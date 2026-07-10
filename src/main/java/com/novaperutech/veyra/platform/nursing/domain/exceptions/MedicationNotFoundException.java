package com.novaperutech.veyra.platform.nursing.domain.exceptions;

/**
 * Exception thrown when a medication is not found.
 * <p>
 * This exception is thrown when a medication is not found in the database.
 * </p>
 * @see RuntimeException
 */
public class MedicationNotFoundException extends RuntimeException {
    /**
     * Constructor for the exception.
     * @param medicationId The ID of the medication that was not found.
     */
    public MedicationNotFoundException(Long medicationId) {
        super(String.format("Medication with ID %s not found.", medicationId));
    }
}
