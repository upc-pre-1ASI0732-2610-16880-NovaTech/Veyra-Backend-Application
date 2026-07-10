package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Medication;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.MedicationAdministration;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllMedicationsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetMedicationAdministrationsByResidentIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetMedicationByIdQuery;

import java.util.List;
import java.util.Optional;

public interface MedicationQueryServices {
    Optional<Medication>handle(GetMedicationByIdQuery query);
    List<Medication>handle(GetAllMedicationsByNursingHomeIdQuery query);
    List<MedicationAdministration>handle(GetMedicationAdministrationsByResidentIdQuery query);
}
