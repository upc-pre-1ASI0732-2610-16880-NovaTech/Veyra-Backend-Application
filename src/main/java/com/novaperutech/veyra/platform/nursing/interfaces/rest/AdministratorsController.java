package com.novaperutech.veyra.platform.nursing.interfaces.rest;

import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAdministratorByIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAdministratorByUserIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.services.AdministratorCommandService;
import com.novaperutech.veyra.platform.nursing.domain.services.AdministratorQueryService;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.AdministratorResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.CreateAdministratorResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.AdministratorResourceFromEntityAssembler;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.CreateAdministratorCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@RestController
@RequestMapping(value = "/api/v1/administrators",produces = APPLICATION_JSON_VALUE)
@Tag(name = "Administrators",description = " Available endpoints for administrators")
public class AdministratorsController {
    private final AdministratorCommandService administratorCommandService;
    private final AdministratorQueryService administratorQueryService;

    public AdministratorsController(AdministratorCommandService administratorCommandService, AdministratorQueryService administratorQueryService) {
        this.administratorCommandService = administratorCommandService;
        this.administratorQueryService = administratorQueryService;
    }

    @PostMapping
    @Operation(summary = "Create administrator", description = "Creates a new Administrator and returns its resource representation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrator created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })

    public ResponseEntity<AdministratorResource>createAdministrator(@Valid @RequestBody CreateAdministratorResource resource){
var createAdministratorCommand= CreateAdministratorCommandFromResourceAssembler.toCommandFromResource(resource);
var administratorId= administratorCommandService.handle(createAdministratorCommand);
if (administratorId==null|| administratorId==0L){ return ResponseEntity.badRequest().build();}
var getAdministratorByIdQuery=new GetAdministratorByIdQuery(administratorId);
var administrator= administratorQueryService.handle(getAdministratorByIdQuery);
if (administrator.isEmpty()){return ResponseEntity.notFound().build();}
var administratorEntity=administrator.get();
var administratorResource= AdministratorResourceFromEntityAssembler.toResourceFromEntity(administratorEntity);
return new ResponseEntity<>(administratorResource, HttpStatus.CREATED);
}

    @GetMapping("/by-user/{userId}")
    @Operation(
            summary = "Get administrator by user id",
            description = "Resolves the Administrator resource (and its own aggregate id) that corresponds to " +
                    "the given IAM user id. The Administrator id is a separate identifier from the user id " +
                    "and must be used for nursing-home-scoped endpoints."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrator found"),
            @ApiResponse(responseCode = "404", description = "No administrator found for the given user id")
    })
    @Parameter(name = "userId", description = "The IAM user id of the administrator", required = true)
    public ResponseEntity<AdministratorResource> getAdministratorByUserId(@PathVariable Long userId) {
        var administrator = administratorQueryService.handle(new GetAdministratorByUserIdQuery(userId));
        if (administrator.isEmpty()) { return ResponseEntity.notFound().build(); }
        var administratorResource = AdministratorResourceFromEntityAssembler.toResourceFromEntity(administrator.get());
        return ResponseEntity.ok(administratorResource);
    }
}
