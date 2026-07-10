package com.novaperutech.veyra.platform.nursing.application.internal.queryservices;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Medication;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.MedicationAdministration;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllMedicationsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetMedicationAdministrationsByResidentIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetMedicationByIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.services.MedicationQueryServices;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.MedicationAdministrationRepository;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.MedicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicationQueryServiceImpl implements MedicationQueryServices {
    private final MedicationRepository medicationRepository;
    private final MedicationAdministrationRepository medicationAdministrationRepository;

    public MedicationQueryServiceImpl(MedicationRepository medicationRepository, MedicationAdministrationRepository medicationAdministrationRepository) {
        this.medicationRepository = medicationRepository;
        this.medicationAdministrationRepository = medicationAdministrationRepository;
    }

    @Override
    public Optional<Medication> handle(GetMedicationByIdQuery query) {
        return medicationRepository.findById(query.id());
    }

    @Override
    public List<Medication> handle(GetAllMedicationsByNursingHomeIdQuery query) {
        return medicationRepository.findByNursingHomeId(query.id());
    }

    @Override
    public List<MedicationAdministration> handle(GetMedicationAdministrationsByResidentIdQuery query) {
        return medicationAdministrationRepository.findByResidentIdOrderByCreatedAtDesc(query.residentId());
    }

}
