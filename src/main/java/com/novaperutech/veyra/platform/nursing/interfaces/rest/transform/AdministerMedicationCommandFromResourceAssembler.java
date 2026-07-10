package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.AdministerMedicationCommand;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.AdministerMedicationResource;

public class AdministerMedicationCommandFromResourceAssembler {
    public static AdministerMedicationCommand toCommandFromResource(AdministerMedicationResource resource, Long medicationId, Long residentId) {
        return new AdministerMedicationCommand(medicationId, residentId, resource.quantity());
    }
}
