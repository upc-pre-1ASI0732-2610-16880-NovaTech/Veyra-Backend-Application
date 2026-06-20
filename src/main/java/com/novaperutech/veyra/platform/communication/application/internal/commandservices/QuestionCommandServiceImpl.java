package com.novaperutech.veyra.platform.communication.application.internal.commandservices;

import com.novaperutech.veyra.platform.communication.domain.model.aggregates.Question;
import com.novaperutech.veyra.platform.communication.domain.model.commands.AnswerQuestionCommand;
import com.novaperutech.veyra.platform.communication.domain.model.commands.CreateQuestionCommand;
import com.novaperutech.veyra.platform.communication.domain.services.QuestionCommandService;
import com.novaperutech.veyra.platform.communication.infrastructure.persistence.jpa.repositories.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionCommandServiceImpl implements QuestionCommandService {

    private final QuestionRepository questionRepository;

    public QuestionCommandServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Long handle(CreateQuestionCommand command) {
        var question = new Question(
                command.relativeId(),
                command.residentId(),
                command.nursingHomeId(),
                command.body()
        );
        return questionRepository.save(question).getId();
    }

    @Override
    public Optional<Question> handle(AnswerQuestionCommand command) {
        var questionOpt = questionRepository.findById(command.questionId());
        if (questionOpt.isEmpty()) return Optional.empty();
        var question = questionOpt.get();
        question.answer(command.answer());
        return Optional.of(questionRepository.save(question));
    }
}
