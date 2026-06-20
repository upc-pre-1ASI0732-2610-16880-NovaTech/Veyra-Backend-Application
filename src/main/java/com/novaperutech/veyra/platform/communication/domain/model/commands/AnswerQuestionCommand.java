package com.novaperutech.veyra.platform.communication.domain.model.commands;

public record AnswerQuestionCommand(Long questionId, String answer) {
    public AnswerQuestionCommand {
        if (questionId == null) throw new IllegalArgumentException("questionId is required");
        if (answer == null || answer.isBlank()) throw new IllegalArgumentException("answer is required");
    }
}
