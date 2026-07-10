package com.novaperutech.veyra.platform.payments.interfaces.rest;

import com.novaperutech.veyra.platform.payments.infrastructure.persistence.stripe.webhooks.StripeWebhookHandler;
import com.stripe.model.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Receives asynchronous Stripe webhook events (subscription lifecycle and payment intent updates).
 * Must stay outside JWT authentication: Stripe signs the payload with {@code Stripe-Signature}
 * instead of sending a bearer token (see WebSecurityConfiguration's permitAll list).
 */
@RestController
@RequestMapping(value = "/api/v1/webhooks/stripe")
@Tag(name = "Payments")
@Slf4j
public class StripeWebhookController {

    private final StripeWebhookHandler stripeWebhookHandler;

    public StripeWebhookController(StripeWebhookHandler stripeWebhookHandler) {
        this.stripeWebhookHandler = stripeWebhookHandler;
    }

    @PostMapping
    @Operation(summary = "Receive a Stripe webhook event", description = "Verifies the Stripe signature and dispatches subscription/payment events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event processed"),
            @ApiResponse(responseCode = "400", description = "Invalid signature or payload")
    })
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signatureHeader) {
        Event event;
        try {
            event = stripeWebhookHandler.constructEvent(payload, signatureHeader);
        } catch (RuntimeException e) {
            log.warn("Rejected Stripe webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        String eventType = event.getType();
        if (eventType.startsWith("customer.subscription")) {
            stripeWebhookHandler.handleSubscriptionEvent(event);
        } else if (eventType.startsWith("payment_intent")) {
            stripeWebhookHandler.handlePaymentEvent(event);
        } else {
            log.info("Ignoring unhandled Stripe event type: {}", eventType);
        }

        return ResponseEntity.ok().build();
    }
}
