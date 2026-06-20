package com.novaperutech.veyra.platform.communication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record AnswerQuestionResource(@NotBlank(message = "answer is required") String answer) {}
