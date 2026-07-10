package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.MedicationAdministration;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.MedicationAdministrationResource;

public class MedicationAdministrationResourceFromEntityAssembler {
    public static MedicationAdministrationResource toResourceFromEntity(MedicationAdministration entity) {
        return new MedicationAdministrationResource(entity.getId(), entity.getMedication().getId(), entity.getMedication().getName(),
                entity.getResident().getId(), entity.getQuantity(), entity.getCreatedAt());
    }
}
