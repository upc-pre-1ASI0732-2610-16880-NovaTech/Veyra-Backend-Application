package com.novaperutech.veyra.platform.nursing.interfaces.rest;

import com.novaperutech.veyra.platform.nursing.domain.model.queries.ExistsByResidentIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllergiesByResidentIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.services.AllergyCommandServices;
import com.novaperutech.veyra.platform.nursing.domain.services.AllergyQueryServices;
import com.novaperutech.veyra.platform.nursing.domain.services.ResidentQueryServices;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.AllergyResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.CreateAllergyResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.AllergyResourceFromEntityAssembler;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.CreateAllergyCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/residents/{residentId}/allergies", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Residents")
public class ResidentAllergiesController {
    private final AllergyQueryServices allergyQueryServices;
    private final AllergyCommandServices allergyCommandServices;
    private final ResidentQueryServices residentQueryServices;

    public ResidentAllergiesController(AllergyQueryServices allergyQueryServices, AllergyCommandServices allergyCommandServices, ResidentQueryServices residentQueryServices) {
        this.allergyQueryServices = allergyQueryServices;
        this.allergyCommandServices = allergyCommandServices;
        this.residentQueryServices = residentQueryServices;
    }

    @GetMapping
    @Operation(summary = "Get allergies for resident", description = "Get the allergies registered for a resident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Allergies retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Resident not found")
    })
    @Parameter(name = "residentId", description = "The unique identifier of the resident", required = true)
    public ResponseEntity<List<AllergyResource>> getAllergiesByResidentId(@PathVariable Long residentId) {
        if (!residentQueryServices.handle(new ExistsByResidentIdQuery(residentId))) {
            return ResponseEntity.notFound().build();
        }
        var allergies = allergyQueryServices.handle(new GetAllergiesByResidentIdQuery(residentId));
        var allergyResources = allergies.stream().map(AllergyResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(allergyResources);
    }

    @PostMapping
    @Operation(summary = "Register a new allergy for resident", description = "Create a new allergy record for a resident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Allergy created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Resident not found")
    })
    @Parameter(name = "residentId", description = "The unique identifier of the resident", required = true)
    public ResponseEntity<AllergyResource> createAllergyForResident(@PathVariable Long residentId, @Valid @RequestBody CreateAllergyResource resource) {
        if (!residentQueryServices.handle(new ExistsByResidentIdQuery(residentId))) {
            return ResponseEntity.notFound().build();
        }
        var command = CreateAllergyCommandFromResourceAssembler.toCommandFromResource(resource, residentId);
        var allergyId = allergyCommandServices.handle(command);
        var allergyQuery = allergyQueryServices.handle(new GetAllergiesByResidentIdQuery(residentId)).stream()
                .filter(a -> a.getId().equals(allergyId))
                .findFirst();
        if (allergyQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var allergyResource = AllergyResourceFromEntityAssembler.toResourceFromEntity(allergyQuery.get());
        return new ResponseEntity<>(allergyResource, HttpStatus.CREATED);
    }
}
