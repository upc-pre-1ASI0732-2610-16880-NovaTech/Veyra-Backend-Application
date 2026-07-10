package com.novaperutech.veyra.platform.nursing.application.internal.commandservices;

import com.novaperutech.veyra.platform.nursing.domain.exceptions.MedicationAlreadyExistsException;
import com.novaperutech.veyra.platform.nursing.domain.exceptions.MedicationCreationException;
import com.novaperutech.veyra.platform.nursing.domain.exceptions.MedicationNotFoundException;
import com.novaperutech.veyra.platform.nursing.domain.exceptions.NursingHomeNotFoundException;
import com.novaperutech.veyra.platform.nursing.domain.exceptions.ResidentNotActiveException;
import com.novaperutech.veyra.platform.nursing.domain.exceptions.ResidentNotBelongToNursingHomeException;
import com.novaperutech.veyra.platform.nursing.domain.exceptions.ResidentNotFoundException;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Medication;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.MedicationAdministration;
import com.novaperutech.veyra.platform.nursing.domain.model.commands.AdministerMedicationCommand;
import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateMedicationCommand;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.DrugPresentation;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.ExpirationDate;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.ResidentState;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.Stock;
import com.novaperutech.veyra.platform.nursing.domain.services.MedicationCommandServices;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.MedicationAdministrationRepository;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.MedicationRepository;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.NursingHomeRepository;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.ResidentRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the MedicationCommandServices interface.
 * <p>This class is responsible for handling the commands related to the Medication aggregate,
 * including creating inventory entries and administering doses to residents.</p>
 * @see MedicationCommandServices
 * @see MedicationRepository
 * @see NursingHomeRepository
 */
@Service
public class MedicationCommandServicesImpl implements MedicationCommandServices {
    private final MedicationRepository medicationRepository;
    private final NursingHomeRepository nursingHomeRepository;
    private final ResidentRepository residentRepository;
    private final MedicationAdministrationRepository medicationAdministrationRepository;

    /**
     * Constructor of the class.
     * @param medicationRepository the repository to be used by the class.
     * @param nursingHomeRepository the repository to be used by the class.
     * @param residentRepository the repository to be used by the class.
     * @param medicationAdministrationRepository the repository to be used by the class.
     */
    public MedicationCommandServicesImpl(MedicationRepository medicationRepository,
                                         NursingHomeRepository nursingHomeRepository,
                                         ResidentRepository residentRepository,
                                         MedicationAdministrationRepository medicationAdministrationRepository) {
        this.medicationRepository = medicationRepository;
        this.nursingHomeRepository = nursingHomeRepository;
        this.residentRepository = residentRepository;
        this.medicationAdministrationRepository = medicationAdministrationRepository;
    }

    // inherit javadoc
    @Override
    public Long handle(CreateMedicationCommand command) {
        // Validate nursing home exists
        var nursingHome = nursingHomeRepository.findById(command.nursingHomeId())
                .orElseThrow(() -> new NursingHomeNotFoundException(command.nursingHomeId()));

        // Validate medication doesn't already exist for this nursing home (same name + lot)
        if (medicationRepository.existsByNursingHomeIdAndNameAndLot(command.nursingHomeId(), command.name(), command.lot())) {
            throw new MedicationAlreadyExistsException(command.name(), command.lot(), command.nursingHomeId());
        }

        // Create value objects and medication
        var stock = new Stock(command.amount());
        var expirationDate = new ExpirationDate(command.expirationDate());
        var drugPresentation = DrugPresentation.valueOf(command.drugPresentation());
        var medication = new Medication(command.name(), command.description(), stock,
                expirationDate, drugPresentation, command.dosage(), command.lot(), nursingHome);

        // Save medication
        try {
            medicationRepository.save(medication);
        } catch (Exception e) {
            throw new MedicationCreationException(e.getMessage());
        }

        return medication.getId();
    }

    // inherit javadoc
    @Override
    public Long handle(AdministerMedicationCommand command) {
        var medication = medicationRepository.findById(command.medicationId())
                .orElseThrow(() -> new MedicationNotFoundException(command.medicationId()));

        var resident = residentRepository.findById(command.residentId())
                .orElseThrow(() -> new ResidentNotFoundException(command.residentId()));

        if (!resident.getResidentStatus().equals(ResidentState.ACTIVE)) {
            throw new ResidentNotActiveException(command.residentId());
        }

        if (!resident.getNursingHome().getId().equals(medication.getNursingHome().getId())) {
            throw new ResidentNotBelongToNursingHomeException(command.residentId(), medication.getNursingHome().getId());
        }

        // Decrease stock; throws IllegalArgumentException (-> 400 with a clear message) if insufficient stock
        medication.decreaseStock(command.quantity());
        medicationRepository.save(medication);

        var administration = new MedicationAdministration(medication, resident, command.quantity());
        medicationAdministrationRepository.save(administration);

        return administration.getId();
    }
}