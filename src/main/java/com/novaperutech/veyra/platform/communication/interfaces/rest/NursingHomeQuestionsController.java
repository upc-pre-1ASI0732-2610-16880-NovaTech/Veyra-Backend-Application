package com.novaperutech.veyra.platform.communication.interfaces.rest;

import com.novaperutech.veyra.platform.communication.domain.model.commands.AnswerQuestionCommand;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.communication.domain.services.QuestionCommandService;
import com.novaperutech.veyra.platform.communication.domain.services.QuestionQueryService;
import com.novaperutech.veyra.platform.communication.interfaces.rest.resources.AnswerQuestionResource;
import com.novaperutech.veyra.platform.communication.interfaces.rest.resources.QuestionResource;
import com.novaperutech.veyra.platform.communication.interfaces.rest.transform.QuestionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/nursing-homes/{nursingHomeId}/questions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Nursing Homes")
public class NursingHomeQuestionsController {

    private final QuestionCommandService questionCommandService;
    private final QuestionQueryService questionQueryService;

    public NursingHomeQuestionsController(QuestionCommandService questionCommandService,
                                          QuestionQueryService questionQueryService) {
        this.questionCommandService = questionCommandService;
        this.questionQueryService = questionQueryService;
    }

    @GetMapping
    @Operation(
            summary = "Get all family questions for a nursing home",
            description = "Returns all questions sent by relatives of residents in this nursing home. Staff can filter by status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No questions found")
    })
    @Parameter(name = "nursingHomeId", description = "The unique identifier of the nursing home", required = true)
    public ResponseEntity<List<QuestionResource>> getQuestions(@PathVariable Long nursingHomeId) {
        var questions = questionQueryService.handle(new GetQuestionsByNursingHomeIdQuery(nursingHomeId));
        if (questions.isEmpty()) return ResponseEntity.notFound().build();
        var resources = questions.stream()
                .map(QuestionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{questionId}/answer")
    @Operation(
            summary = "Answer a family question",
            description = "Allows authorized staff to answer a pending question from a family member."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question answered successfully"),
            @ApiResponse(responseCode = "400", description = "Question already answered or invalid body"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @Parameter(name = "nursingHomeId", description = "The unique identifier of the nursing home", required = true)
    @Parameter(name = "questionId", description = "The unique identifier of the question", required = true)
    public ResponseEntity<QuestionResource> answerQuestion(
            @PathVariable Long nursingHomeId,
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerQuestionResource resource) {

        var command = new AnswerQuestionCommand(questionId, resource.answer());
        var question = questionCommandService.handle(command);
        if (question.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(QuestionResourceFromEntityAssembler.toResourceFromEntity(question.get()));
    }
}
