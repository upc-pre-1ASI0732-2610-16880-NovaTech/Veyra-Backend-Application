package com.novaperutech.veyra.platform.payments.application.acl;

import com.novaperutech.veyra.platform.payments.domain.model.queries.GetSubscriptionActiveByUserId;
import com.novaperutech.veyra.platform.payments.domain.model.valueobjects.UserId;
import com.novaperutech.veyra.platform.payments.domain.services.SubscriptionQueryService;
import com.novaperutech.veyra.platform.payments.interfaces.acl.PaymentsContextFacade;
import org.springframework.stereotype.Service;

@Service
public class PaymentsContextFacadeImpl implements PaymentsContextFacade {

    private final SubscriptionQueryService subscriptionQueryService;

    public PaymentsContextFacadeImpl(SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionQueryService = subscriptionQueryService;
    }

    @Override
    public boolean hasActiveSubscription(Long userId) {
        return subscriptionQueryService.handle(new GetSubscriptionActiveByUserId(new UserId(userId))).isPresent();
    }
}
