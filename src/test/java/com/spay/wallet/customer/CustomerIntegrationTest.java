package com.spay.wallet.customer;

import com.spay.wallet.customer.reqRes.CustomerRegistrationRequest;
import com.spay.wallet.customer.services.CustomerRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerIntegrationTest {
    @Autowired
    private CustomerRegistrationService customerRegistrationService;

    @Test
    void itShouldName() {
        // Given
//        CustomerRegistrationRequest request =  new CustomerRegistrationRequest(
//                "Ali",
//                "Edle",
//                "Mahamuud",
//                "Somalia",
//                "SO",
//                "+251615111111",
//                "ali@gmail.com"
//        );
        // When
//       customerRegistrationService.registerNewCustomer(request);
        // Then

    }
}
