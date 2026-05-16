package com.novaperutech.veyra.platform;

import com.novaperutech.veyra.platform.payments.infrastructure.persistence.stripe.service.StripeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VeyraPlatformApplicationTests {

    @MockBean
    private StripeService stripeService;

    @Test
    void shouldLoadApplicationContext() {
    }

}
