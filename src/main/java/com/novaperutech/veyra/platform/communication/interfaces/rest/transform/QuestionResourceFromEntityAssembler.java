package com.novaperutech.veyra.platform.communication.interfaces.rest.transform;

import com.novaperutech.veyra.platform.communication.domain.model.aggregates.Question;
import com.novaperutech.veyra.platform.communication.interfaces.rest.resources.QuestionResource;

public class QuestionResourceFromEntityAssembler {
    public static QuestionResource toResourceFromEntity(Question entity) {
        return new QuestionResource(
                entity.getId(),
                entity.getRelativeId(),
                entity.getResidentId(),
                entity.getNursingHomeId(),
                entity.getBody(),
                entity.getAnswer(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
