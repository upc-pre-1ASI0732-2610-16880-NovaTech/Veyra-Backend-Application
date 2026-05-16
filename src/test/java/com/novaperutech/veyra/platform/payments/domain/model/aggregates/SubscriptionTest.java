package com.novaperutech.veyra.platform.payments.domain.model.aggregates;

import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {

  @Test
  void shouldCreateSubscriptionCorrectly() {

    Subscription subscription = new Subscription(
      new UserId(1L),
      "sub_123",
      "cus_123",
      PlanType.FAMILY,
      SubscriptionPeriod.MONTHLY
    );

    assertNotNull(subscription);

    assertEquals("sub_123",
      subscription.getStripeSubscriptionId());

    assertEquals(SubscriptionStatus.INCOMPLETE,
      subscription.getStatus());
  }

  @Test
  void shouldActivateSubscription() {

    Subscription subscription = new Subscription(
      new UserId(1L),
      "sub_123",
      "cus_123",
      PlanType.FAMILY,
      SubscriptionPeriod.MONTHLY
    );

    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusMonths(1);

    subscription.activate(start, end);

    assertEquals(SubscriptionStatus.ACTIVE,
      subscription.getStatus());

    assertTrue(subscription.isActive());
  }

  @Test
  void shouldCancelSubscription() {

    Subscription subscription = new Subscription(
      new UserId(1L),
      "sub_123",
      "cus_123",
      PlanType.FAMILY,
      SubscriptionPeriod.MONTHLY
    );

    subscription.cancel();

    assertEquals(SubscriptionStatus.CANCELED,
      subscription.getStatus());

    assertNotNull(subscription.getCanceledAt());
  }
}
