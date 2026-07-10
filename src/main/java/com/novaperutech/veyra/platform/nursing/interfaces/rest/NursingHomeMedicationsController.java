package com.novaperutech.veyra.platform.nursing.interfaces.rest;

import com.novaperutech.veyra.platform.nursing.domain.model.queries.ExistsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllMedicationsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetMedicationByIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.services.MedicationCommandServices;
import com.novaperutech.veyra.platform.nursing.domain.services.MedicationQueryServices;
import com.novaperutech.veyra.platform.nursing.domain.services.NursingHomeQueryServices;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.CreateMedicationResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.MedicationResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.CreateMedicationCommandFromResourceAssembler;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.MedicationResourceFromEntityAssembler;
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
 * REST controller exposing the shared medication inventory of a nursing home.
 * <p>Medications belong to the nursing home (not to a single resident); individual
 * intakes are tracked separately via {@code MedicationAdministrationsController}.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/nursing-homes/{nursingHomeId}/medications", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Nursing Homes")
public class NursingHomeMedicationsController {
    private final MedicationQueryServices medicationQueryServices;
    private final MedicationCommandServices medicationCommandServices;
    private final NursingHomeQueryServices nursingHomeQueryServices;

    public NursingHomeMedicationsController(MedicationQueryServices medicationQueryServices, MedicationCommandServices medicationCommandServices, NursingHomeQueryServices nursingHomeQueryServices) {
        this.medicationQueryServices = medicationQueryServices;
        this.medicationCommandServices = medicationCommandServices;
        this.nursingHomeQueryServices = nursingHomeQueryServices;
    }

    @GetMapping
    @Operation(summary = "Get medication inventory for nursing home", description = "Get the shared medication inventory for a nursing home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medications retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Nursing home not found")
    })
    @Parameter(name = "nursingHomeId", description = "The unique identifier of the nursing home", required = true)
    public ResponseEntity<List<MedicationResource>> getAllMedicationsByNursingHomeId(@PathVariable Long nursingHomeId) {
        if (!nursingHomeQueryServices.handle(new ExistsByNursingHomeIdQuery(nursingHomeId))) {
            return ResponseEntity.notFound().build();
        }
        var getAllMedicationsByNursingHomeIdQuery = new GetAllMedicationsByNursingHomeIdQuery(nursingHomeId);
        var medications = medicationQueryServices.handle(getAllMedicationsByNursingHomeIdQuery);
        var medicationResources = medications.stream().map(MedicationResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(medicationResources);
    }

    @PostMapping
    @Operation(summary = "Add a medication to the nursing home inventory", description = "Create a new medication entry (with lot) in the shared inventory of a nursing home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medication created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @Parameter(name = "nursingHomeId", description = "The unique identifier of the nursing home", required = true)
    public ResponseEntity<MedicationResource> createMedicationForNursingHome(@PathVariable Long nursingHomeId, @Valid @RequestBody CreateMedicationResource resource) {
        var medicationCommand = CreateMedicationCommandFromResourceAssembler.toCommandFromResource(resource, nursingHomeId);
        var medicationId = medicationCommandServices.handle(medicationCommand);
        if (medicationId == null || medicationId == 0L) {
            return ResponseEntity.badRequest().build();
        }
        var medicationFindByIdQuery = medicationQueryServices.handle(new GetMedicationByIdQuery(medicationId));
        if (medicationFindByIdQuery.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var medicationResource = MedicationResourceFromEntityAssembler.toResourceFromEntity(medicationFindByIdQuery.get());
        return new ResponseEntity<>(medicationResource, HttpStatus.CREATED);
    }
}
