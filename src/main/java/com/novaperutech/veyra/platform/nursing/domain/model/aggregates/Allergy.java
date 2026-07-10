package com.novaperutech.veyra.platform.nursing.domain.model.aggregates;

import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.AllergySeverity;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.AllergyType;
import com.novaperutech.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.novaperutech.veyra.platform.shared.infrastructure.persistence.jpa.converters.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Aggregate root representing an allergy registered for a resident.
 * Sensitive fields (allergen name, reaction) are encrypted at rest (US38).
 */
@Entity
@Getter
public class Allergy extends AuditableAbstractAggregateRoot<Allergy> {

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String allergenName;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllergySeverity severityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllergyType typeOfAllergy;

    public Allergy() {}

    public Allergy(Resident resident, String allergenName, String reaction, AllergySeverity severityLevel, AllergyType typeOfAllergy) {
        this.resident = resident;
        this.allergenName = allergenName;
        this.reaction = reaction;
        this.severityLevel = severityLevel;
        this.typeOfAllergy = typeOfAllergy;
    }
}
