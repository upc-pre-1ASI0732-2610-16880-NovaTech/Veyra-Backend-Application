package com.novaperutech.veyra.platform.communication.infrastructure.persistence.jpa.repositories;

import com.novaperutech.veyra.platform.communication.domain.model.aggregates.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByRelativeId(Long relativeId);
    List<Question> findAllByNursingHomeId(Long nursingHomeId);
}
