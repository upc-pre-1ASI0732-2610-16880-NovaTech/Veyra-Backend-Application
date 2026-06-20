package com.novaperutech.veyra.platform.communication.application.internal.queryservices;

import com.novaperutech.veyra.platform.communication.domain.model.aggregates.Question;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionByIdQuery;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionsByRelativeIdQuery;
import com.novaperutech.veyra.platform.communication.domain.services.QuestionQueryService;
import com.novaperutech.veyra.platform.communication.infrastructure.persistence.jpa.repositories.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionQueryServiceImpl implements QuestionQueryService {

    private final QuestionRepository questionRepository;

    public QuestionQueryServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Question> handle(GetQuestionByIdQuery query) {
        return questionRepository.findById(query.questionId());
    }

    @Override
    public List<Question> handle(GetQuestionsByRelativeIdQuery query) {
        return questionRepository.findAllByRelativeId(query.relativeId());
    }

    @Override
    public List<Question> handle(GetQuestionsByNursingHomeIdQuery query) {
        return questionRepository.findAllByNursingHomeId(query.nursingHomeId());
    }
}
