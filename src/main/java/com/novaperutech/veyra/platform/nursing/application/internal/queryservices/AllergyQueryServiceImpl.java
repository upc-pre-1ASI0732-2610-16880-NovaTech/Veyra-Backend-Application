package com.novaperutech.veyra.platform.nursing.application.internal.queryservices;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Allergy;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllergiesByResidentIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.services.AllergyQueryServices;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.AllergyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllergyQueryServiceImpl implements AllergyQueryServices {
    private final AllergyRepository allergyRepository;

    public AllergyQueryServiceImpl(AllergyRepository allergyRepository) {
        this.allergyRepository = allergyRepository;
    }

    @Override
    public List<Allergy> handle(GetAllergiesByResidentIdQuery query) {
        return allergyRepository.findByResidentId(query.residentId());
    }
}
