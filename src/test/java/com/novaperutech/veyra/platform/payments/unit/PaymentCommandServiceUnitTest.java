package com.novaperutech.veyra.platform.payments.unit;

import com.novaperutech.veyra.platform.payments.application.internal.commandservices.PaymentCommandServiceImpl;
import com.novaperutech.veyra.platform.payments.domain.model.aggregates.Subscription;
import com.novaperutech.veyra.platform.payments.domain.model.commands.ProcessPaymentCommand;
import com.novaperutech.veyra.platform.payments.domain.model.events.PaymentSucceededEvent;
import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.PaymentStatus;
import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.PlanType;
import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.SubscriptionPeriod;
import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.UserId;
import com.novaperutech.veyra.platform.payments.infrastructure.persistence.jpa.repositories.PaymentRepository;
import com.novaperutech.veyra.platform.payments.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import com.novaperutech.veyra.platform.payments.infrastructure.persistence.stripe.service.StripeService;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private StripeService stripeService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PaymentIntent paymentIntent;

    @Mock
    private Charge charge;

    @InjectMocks
    private PaymentCommandServiceImpl paymentCommandService;

    @Test
    void shouldCreatePaymentSuccessfully() {
        var subscription = new Subscription(new UserId(1L), "sub_123", "cus_123", PlanType.FAMILY, SubscriptionPeriod.MONTHLY);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(stripeService.createPaymentIntent(any(Long.class), any(String.class), any(String.class))).thenReturn(paymentIntent);
        when(paymentIntent.getId()).thenReturn("pi_123");
        when(paymentIntent.getStatus()).thenReturn("succeeded");
        when(paymentIntent.getLatestChargeObject()).thenReturn(charge);
        when(charge.getReceiptUrl()).thenReturn("https://stripe.test/receipt/123");
        when(paymentRepository.save(any(com.novaperutech.veyra.platform.payments.domain.model.aggregates.Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = paymentCommandService.handle(new ProcessPaymentCommand(1L, "pm_123"));

        assertTrue(result.isPresent());
        assertEquals(PaymentStatus.SUCCEEDED, result.get().getStatus());
        assertEquals("pi_123", result.get().getStripePaymentIntentId());
        verify(subscriptionRepository).findById(1L);
        verify(stripeService).createPaymentIntent(any(Long.class), any(String.class), any(String.class));
        verify(paymentRepository).save(any(com.novaperutech.veyra.platform.payments.domain.model.aggregates.Payment.class));
        verify(eventPublisher).publishEvent(any(PaymentSucceededEvent.class));
    }

    @Test
    void shouldHandleStripePaymentFailure() {
        var subscription = new Subscription(new UserId(1L), "sub_123", "cus_123", PlanType.FAMILY, SubscriptionPeriod.MONTHLY);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(stripeService.createPaymentIntent(any(Long.class), any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("stripe unavailable"));

        var exception = assertThrows(RuntimeException.class,
                () -> paymentCommandService.handle(new ProcessPaymentCommand(1L, "pm_123")));

        assertEquals("Failed to process payment", exception.getMessage());
        verify(subscriptionRepository).findById(1L);
        verify(stripeService).createPaymentIntent(any(Long.class), any(String.class), any(String.class));
        verify(paymentRepository, never()).save(any(com.novaperutech.veyra.platform.payments.domain.model.aggregates.Payment.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
}
