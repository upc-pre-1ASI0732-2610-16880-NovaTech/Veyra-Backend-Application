package com.novaperutech.veyra.platform.communication.domain.services;

import com.novaperutech.veyra.platform.communication.domain.model.aggregates.Question;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionByIdQuery;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionsByRelativeIdQuery;

import java.util.List;
import java.util.Optional;

public interface QuestionQueryService {
    Optional<Question> handle(GetQuestionByIdQuery query);
    List<Question> handle(GetQuestionsByRelativeIdQuery query);
    List<Question> handle(GetQuestionsByNursingHomeIdQuery query);
}
