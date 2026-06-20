package com.novaperutech.veyra.platform.payments.interfaces.acl;

public interface PaymentsContextFacade {
    boolean hasActiveSubscription(Long userId);
}
