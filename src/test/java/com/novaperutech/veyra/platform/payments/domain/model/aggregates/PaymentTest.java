package com.novaperutech.veyra.platform.payments.domain.model.aggregates;

import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

  @Test
  void shouldCreatePaymentCorrectly() {

    Subscription subscription = null;

    Payment payment = new Payment(
      subscription,
      new UserId(1L),
      "pi_123",
      new Amount(100.0, "USD")
    );

    assertEquals(PaymentStatus.PENDING,
      payment.getStatus());
  }

  @Test
  void shouldMarkPaymentAsSucceeded() {

    Payment payment = new Payment(
      null,
      new UserId(1L),
      "pi_123",
      new Amount(100.0, "USD")
    );

    payment.markAsSucceeded("receipt-url");

    assertEquals(PaymentStatus.SUCCEEDED,
      payment.getStatus());

    assertEquals("receipt-url",
      payment.getStripeReceiptUrl());
  }

  @Test
  void shouldMarkPaymentAsFailed() {

    Payment payment = new Payment(
      null,
      new UserId(1L),
      "pi_123",
      new Amount(100.0, "USD")
    );

    payment.markAsFailed("Card declined");

    assertEquals(PaymentStatus.FAILED,
      payment.getStatus());

    assertEquals("Card declined",
      payment.getFailureMessage());
  }
}
