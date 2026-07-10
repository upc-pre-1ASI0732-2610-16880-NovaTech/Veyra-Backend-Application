/**
 * Aggregate root representing a single administration (intake) of a medication
 * to a resident, decremented from the nursing home's shared medication inventory.
 *
 * @summary Represents an audit record of a medication dose given to a resident.
 */
package com.novaperutech.veyra.platform.nursing.domain.model.aggregates;

import com.novaperutech.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class MedicationAdministration extends AuditableAbstractAggregateRoot<MedicationAdministration> {

    @ManyToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Resident resident;

    private Integer quantity;

    public MedicationAdministration() {}

    public MedicationAdministration(Medication medication, Resident resident, Integer quantity) {
        this.medication = medication;
        this.resident = resident;
        this.quantity = quantity;
    }
}
