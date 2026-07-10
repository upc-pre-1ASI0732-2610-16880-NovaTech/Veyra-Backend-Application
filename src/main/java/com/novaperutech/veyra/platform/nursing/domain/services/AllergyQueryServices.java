package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Allergy;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllergiesByResidentIdQuery;

import java.util.List;

public interface AllergyQueryServices {
    List<Allergy> handle(GetAllergiesByResidentIdQuery query);
}
