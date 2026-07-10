package com.novaperutech.veyra.platform.nursing.domain.services;

import com.novaperutech.veyra.platform.nursing.domain.model.commands.CreateAllergyCommand;

public interface AllergyCommandServices {
    Long handle(CreateAllergyCommand command);
}
