package com.novaperutech.veyra.platform.nursing.interfaces.rest.transform;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Medication;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.MedicationAlertResource;

import java.util.ArrayList;
import java.util.List;

public class MedicationAlertResourceAssembler {

    private static final int EXPIRING_SOON_DAYS = 30;

    public static List<MedicationAlertResource> fromMedications(List<Medication> medications) {
        var alerts = new ArrayList<MedicationAlertResource>();
        for (var medication : medications) {
            if (medication.isLowStock()) {
                alerts.add(new MedicationAlertResource(
                        medication.getId(), medication.getName(), "LOW_STOCK",
                        String.format("Low stock for '%s' (lot %s): %d units remaining",
                                medication.getName(), medication.getLot(), medication.getStock().amount())
                ));
            }
            if (medication.isExpiringSoon(EXPIRING_SOON_DAYS)) {
                alerts.add(new MedicationAlertResource(
                        medication.getId(), medication.getName(), "EXPIRING_SOON",
                        String.format("'%s' (lot %s) expires on %s",
                                medication.getName(), medication.getLot(), medication.getExpirationDate().expirationDate())
                ));
            }
        }
        return alerts;
    }
}
