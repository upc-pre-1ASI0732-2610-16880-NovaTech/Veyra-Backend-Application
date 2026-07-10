package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateAllergyCommand;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.CreateAllergyResource;

public class CreateAllergyCommandFromResourceAssembler {
    public static CreateAllergyCommand toCommandFromResource(CreateAllergyResource resource, Long residentId) {
        return new CreateAllergyCommand(residentId, resource.allergenName(), resource.reaction(), resource.severityLevel(), resource.typeOfAllergy());
    }
}
