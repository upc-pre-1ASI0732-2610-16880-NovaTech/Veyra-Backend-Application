package com.novaperutech.veyra.platform.communication.domain.model.aggregates;

import com.novaperutech.veyra.platform.communication.domain.model.valueobjects.QuestionStatus;
import com.novaperutech.veyra.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.novaperutech.veyra.platform.shared.infrastructure.persistence.jpa.converters.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "questions")
public class Question extends AuditableAbstractAggregateRoot<Question> {

    @Column(nullable = false)
    private Long relativeId;

    @Column(nullable = false)
    private Long residentId;

    @Column(nullable = false)
    private Long nursingHomeId;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status;

    protected Question() {}

    public Question(Long relativeId, Long residentId, Long nursingHomeId, String body) {
        if (relativeId == null) throw new IllegalArgumentException("relativeId cannot be null");
        if (residentId == null) throw new IllegalArgumentException("residentId cannot be null");
        if (nursingHomeId == null) throw new IllegalArgumentException("nursingHomeId cannot be null");
        if (body == null || body.isBlank()) throw new IllegalArgumentException("body cannot be blank");
        this.relativeId = relativeId;
        this.residentId = residentId;
        this.nursingHomeId = nursingHomeId;
        this.body = body;
        this.status = QuestionStatus.PENDING;
    }

    public void answer(String answerText) {
        if (answerText == null || answerText.isBlank()) throw new IllegalArgumentException("answer cannot be blank");
        if (this.status == QuestionStatus.ANSWERED) throw new IllegalStateException("Question already answered");
        this.answer = answerText;
        this.status = QuestionStatus.ANSWERED;
    }
}
