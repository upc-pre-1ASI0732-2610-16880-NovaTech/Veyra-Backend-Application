package com.novaperutech.veyra.platform.nursing.interfaces.rest;

import com.novaperutech.veyra.platform.nursing.domain.model.queries.ExistsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAllMedicationsByNursingHomeIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetRoomsForNursingHomeIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.services.MedicationQueryServices;
import com.novaperutech.veyra.platform.nursing.domain.services.NursingHomeQueryServices;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.MedicationAlertResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.resources.OccupancyResource;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.MedicationAlertResourceAssembler;
import com.novaperutech.veyra.platform.nursing.interfaces.rest.transform.OccupancyResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller exposing real-time dashboard indicators (occupancy and critical alerts)
 * for a nursing home, complementing the historical charts served by the Analytics bounded context.
 */
@RestController
@RequestMapping(value = "/api/v1/nursing-homes/{nursingHomeId}/dashboard", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Nursing Homes")
public class NursingHomeDashboardController {
    private final NursingHomeQueryServices nursingHomeQueryServices;
    private final MedicationQueryServices medicationQueryServices;

    public NursingHomeDashboardController(NursingHomeQueryServices nursingHomeQueryServices, MedicationQueryServices medicationQueryServices) {
        this.nursingHomeQueryServices = nursingHomeQueryServices;
        this.medicationQueryServices = medicationQueryServices;
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Get nursing home occupancy", description = "Returns the current room occupancy (capacity, occupied and available slots, and occupancy rate) for a nursing home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Occupancy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Nursing home not found")
    })
    @Parameter(name = "nursingHomeId", description = "The unique identifier of the nursing home", required = true)
    public ResponseEntity<OccupancyResource> getOccupancy(@PathVariable Long nursingHomeId) {
        if (!nursingHomeQueryServices.handle(new ExistsByNursingHomeIdQuery(nursingHomeId))) {
            return ResponseEntity.notFound().build();
        }
        var rooms = nursingHomeQueryServices.handle(new GetRoomsForNursingHomeIdQuery(nursingHomeId));
        return ResponseEntity.ok(OccupancyResourceAssembler.fromRooms(rooms));
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get nursing home critical alerts", description = "Returns critical alerts for a nursing home: medications with low stock or nearing expiration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Nursing home not found")
    })
    @Parameter(name = "nursingHomeId", description = "The unique identifier of the nursing home", required = true)
    public ResponseEntity<List<MedicationAlertResource>> getAlerts(@PathVariable Long nursingHomeId) {
        if (!nursingHomeQueryServices.handle(new ExistsByNursingHomeIdQuery(nursingHomeId))) {
            return ResponseEntity.notFound().build();
        }
        var medications = medicationQueryServices.handle(new GetAllMedicationsByNursingHomeIdQuery(nursingHomeId));
        return ResponseEntity.ok(MedicationAlertResourceAssembler.fromMedications(medications));
    }
}
