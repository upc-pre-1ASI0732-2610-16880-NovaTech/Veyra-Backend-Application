package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Allergy;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.AllergyResource;

public class AllergyResourceFromEntityAssembler {
    public static AllergyResource toResourceFromEntity(Allergy entity) {
        return new AllergyResource(entity.getId(), entity.getResident().getId(), entity.getAllergenName(),
                entity.getReaction(), entity.getSeverityLevel().name(), entity.getTypeOfAllergy().name());
    }
}
