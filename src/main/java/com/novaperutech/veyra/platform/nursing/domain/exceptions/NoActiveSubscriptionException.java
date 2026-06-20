package com.novaperutech.veyra.platform.nursing.domain.exceptions;

public class NoActiveSubscriptionException extends RuntimeException {
    public NoActiveSubscriptionException(Long administratorId) {
        super(String.format("Administrator with id %d does not have an active subscription.", administratorId));
    }
}
