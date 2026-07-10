package com.novaperutech.veyra.platform.nursing.application.internal.commandservices;

import com.novaperutech.veyra.platform.nursing.domain.exceptions.ResidentNotFoundException;
import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Allergy;
import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateAllergyCommand;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.AllergySeverity;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.AllergyType;
import com.novaperutech.veyra.platform.nursing.domain.services.AllergyCommandServices;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.AllergyRepository;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.ResidentRepository;
import org.springframework.stereotype.Service;

@Service
public class AllergyCommandServicesImpl implements AllergyCommandServices {
    private final AllergyRepository allergyRepository;
    private final ResidentRepository residentRepository;

    public AllergyCommandServicesImpl(AllergyRepository allergyRepository, ResidentRepository residentRepository) {
        this.allergyRepository = allergyRepository;
        this.residentRepository = residentRepository;
    }

    @Override
    public Long handle(CreateAllergyCommand command) {
        var resident = residentRepository.findById(command.residentId())
                .orElseThrow(() -> new ResidentNotFoundException(command.residentId()));

        var severity = AllergySeverity.valueOf(command.severityLevel());
        var type = AllergyType.valueOf(command.typeOfAllergy());

        var allergy = new Allergy(resident, command.allergenName(), command.reaction(), severity, type);
        allergyRepository.save(allergy);
        return allergy.getId();
    }
}
