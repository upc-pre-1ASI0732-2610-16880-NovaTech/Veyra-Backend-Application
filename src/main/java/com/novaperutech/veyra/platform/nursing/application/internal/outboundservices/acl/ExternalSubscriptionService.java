package com.novaperutech.veyra.platform.nursing.application.internal.outboundservices.acl;

import com.novaperutech.veyra.platform.payments.interfaces.acl.PaymentsContextFacade;
import org.springframework.stereotype.Service;

@Service
public class ExternalSubscriptionService {

    private final PaymentsContextFacade paymentsContextFacade;

    public ExternalSubscriptionService(PaymentsContextFacade paymentsContextFacade) {
        this.paymentsContextFacade = paymentsContextFacade;
    }

    public boolean hasActiveSubscription(Long userId) {
        return paymentsContextFacade.hasActiveSubscription(userId);
    }
}
