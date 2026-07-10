package com.novaperutech.veyra.platform.nursing.interfaces.rest;

import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetMedicationAdministrationsByResidentIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.services.MedicationCommandServices;
import com.novaperutech.veyra.platform.nursing.domain.services.MedicationQueryServices;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.AdministerMedicationResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.MedicationAdministrationResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.AdministerMedicationCommandFromResourceAssembler;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.MedicationAdministrationResourceFromEntityAssembler;
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

/**
 * REST controller for registering and querying medication intakes (administrations)
 * given to a resident, decrementing the nursing home's shared medication inventory.
 */
@RestController
@RequestMapping(value = "/api/v1/residents/{residentId}/medications/{medicationId}/administrations", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Residents")
public class MedicationAdministrationsController {
    private final MedicationCommandServices medicationCommandServices;
    private final MedicationQueryServices medicationQueryServices;

    public MedicationAdministrationsController(MedicationCommandServices medicationCommandServices, MedicationQueryServices medicationQueryServices) {
        this.medicationCommandServices = medicationCommandServices;
        this.medicationQueryServices = medicationQueryServices;
    }

    @PostMapping
    @Operation(summary = "Register a medication intake", description = "Registers that a resident received a dose of a medication, decreasing the shared inventory stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Intake registered"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock or invalid request")
    })
    @Parameter(name = "residentId", description = "The unique identifier of the resident", required = true)
    @Parameter(name = "medicationId", description = "The unique identifier of the medication", required = true)
    public ResponseEntity<Void> administerMedication(@PathVariable Long residentId, @PathVariable Long medicationId, @Valid @RequestBody AdministerMedicationResource resource) {
        var command = AdministerMedicationCommandFromResourceAssembler.toCommandFromResource(resource, medicationId, residentId);
        medicationCommandServices.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Get medication intake history for a resident", description = "Get the registered medication intakes for a resident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intake history retrieved successfully")
    })
    @Parameter(name = "residentId", description = "The unique identifier of the resident", required = true)
    public ResponseEntity<List<MedicationAdministrationResource>> getAdministrationsByResident(@PathVariable Long residentId, @PathVariable Long medicationId) {
        var administrations = medicationQueryServices.handle(new GetMedicationAdministrationsByResidentIdQuery(residentId)).stream()
                .filter(administration -> administration.getMedication().getId().equals(medicationId))
                .map(MedicationAdministrationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(administrations);
    }
}
