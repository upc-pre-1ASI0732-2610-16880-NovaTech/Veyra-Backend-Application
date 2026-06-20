package com.novaperutech.veyra.platform.payments.interfaces.rest;

import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.PlanType;
import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.SubscriptionPeriod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/plans", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Plans", description = "Public endpoint to view available subscription plans")
public class PlansController {

    public record PlanFeatureResource(String name, boolean included) {}

    public record PlanPriceResource(String period, double price, String currency) {}

    public record PlanResource(
            String id,
            String displayName,
            String description,
            List<PlanPriceResource> prices,
            List<PlanFeatureResource> features
    ) {}

    @GetMapping
    @Operation(
            summary = "Get all available plans",
            description = "Returns the list of subscription plans with pricing and features. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plans retrieved successfully")
    })
    public ResponseEntity<List<PlanResource>> getPlans() {
        var plans = Arrays.stream(PlanType.values())
                .map(this::toPlanResource)
                .toList();
        return ResponseEntity.ok(plans);
    }

    private PlanResource toPlanResource(PlanType plan) {
        var prices = List.of(
                new PlanPriceResource("MONTHLY", plan.getMonthlyPrice(), "USD"),
                new PlanPriceResource("ANNUALLY", plan.getAnnualPrice(), "USD")
        );

        var features = switch (plan) {
            case FAMILY -> List.of(
                    new PlanFeatureResource("Resident profile access", true),
                    new PlanFeatureResource("Daily activity updates", true),
                    new PlanFeatureResource("Send questions to staff", true),
                    new PlanFeatureResource("Medication tracking view", true),
                    new PlanFeatureResource("Multi-resident management", false)
            );
            case NURSING_HOME -> List.of(
                    new PlanFeatureResource("Full resident management", true),
                    new PlanFeatureResource("Staff management (HCM)", true),
                    new PlanFeatureResource("Medication inventory", true),
                    new PlanFeatureResource("Analytics dashboard", true),
                    new PlanFeatureResource("Family communication portal", true)
            );
        };

        return new PlanResource(plan.name(), plan.getDisplayName(), descriptionFor(plan), prices, features);
    }

    private String descriptionFor(PlanType plan) {
        return switch (plan) {
            case FAMILY -> "For families who want to stay connected with their loved one's daily care.";
            case NURSING_HOME -> "Complete management suite for nursing home administrators and staff.";
        };
    }
}
