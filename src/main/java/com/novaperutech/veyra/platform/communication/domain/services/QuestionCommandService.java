package com.novaperutech.veyra.platform.communication.domain.services;

import com.novaperutech.veyra.platform.communication.domain.model.aggregates.Question;
import com.novaperutech.veyra.platform.communication.domain.model.commands.AnswerQuestionCommand;
import com.novaperutech.veyra.platform.communication.domain.model.commands.CreateQuestionCommand;

import java.util.Optional;

public interface QuestionCommandService {
    Long handle(CreateQuestionCommand command);
    Optional<Question> handle(AnswerQuestionCommand command);
}
