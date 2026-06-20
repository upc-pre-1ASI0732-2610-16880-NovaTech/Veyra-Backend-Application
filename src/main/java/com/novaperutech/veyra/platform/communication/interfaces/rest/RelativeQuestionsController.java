package com.novaperutech.veyra.platform.communication.interfaces.rest;

import com.novaperutech.veyra.platform.communication.domain.model.commands.CreateQuestionCommand;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionByIdQuery;
import com.novaperutech.veyra.platform.communication.domain.model.queries.GetQuestionsByRelativeIdQuery;
import com.novaperutech.veyra.platform.communication.domain.services.QuestionCommandService;
import com.novaperutech.veyra.platform.communication.domain.services.QuestionQueryService;
import com.novaperutech.veyra.platform.communication.interfaces.rest.resources.CreateQuestionResource;
import com.novaperutech.veyra.platform.communication.interfaces.rest.resources.QuestionResource;
import com.novaperutech.veyra.platform.communication.interfaces.rest.transform.QuestionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/relatives/{relativeId}/questions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Relatives")
public class RelativeQuestionsController {

    private final QuestionCommandService questionCommandService;
    private final QuestionQueryService questionQueryService;

    public RelativeQuestionsController(QuestionCommandService questionCommandService,
                                       QuestionQueryService questionQueryService) {
        this.questionCommandService = questionCommandService;
        this.questionQueryService = questionQueryService;
    }

    @PostMapping
    @Operation(
            summary = "Send a question about a resident's daily routine",
            description = "Allows a family member to submit a question to the nursing home staff about their loved one."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @Parameter(name = "relativeId", description = "The unique identifier of the relative", required = true)
    public ResponseEntity<QuestionResource> createQuestion(
            @PathVariable Long relativeId,
            @Valid @RequestBody CreateQuestionResource resource) {

        var command = new CreateQuestionCommand(
                relativeId,
                resource.residentId(),
                resource.nursingHomeId(),
                resource.body()
        );
        var questionId = questionCommandService.handle(command);
        if (questionId == null || questionId == 0L) return ResponseEntity.badRequest().build();

        var question = questionQueryService.handle(new GetQuestionByIdQuery(questionId));
        if (question.isEmpty()) return ResponseEntity.notFound().build();

        return new ResponseEntity<>(QuestionResourceFromEntityAssembler.toResourceFromEntity(question.get()), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get all questions sent by a relative",
            description = "Returns all questions (pending and answered) submitted by the given relative."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No questions found")
    })
    @Parameter(name = "relativeId", description = "The unique identifier of the relative", required = true)
    public ResponseEntity<List<QuestionResource>> getQuestionsByRelative(@PathVariable Long relativeId) {
        var questions = questionQueryService.handle(new GetQuestionsByRelativeIdQuery(relativeId));
        if (questions.isEmpty()) return ResponseEntity.notFound().build();
        var resources = questions.stream()
                .map(QuestionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
