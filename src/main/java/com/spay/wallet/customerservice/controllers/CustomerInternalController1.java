package com.spay.wallet.customerservice.controllers;


import com.spay.wallet.customerservice.clients.BasicCustomerRegistrationRequest;
import com.spay.wallet.customerservice.clients.CompletedCustomerRegistrationRequest;
//import com.spay.wallet.customerservice.common.security.UserAuthorize;
import com.spay.wallet.customerservice.services.CustomerRegistrationService1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/micro_to_mono_apis_create_customer")
@RequiredArgsConstructor
public class CustomerInternalController1 {
    private final CustomerRegistrationService1 registrationService;

    @PostMapping
    public ResponseEntity<String> createBasicCustomer(@RequestBody  BasicCustomerRegistrationRequest request){
        return ResponseEntity.ok(registrationService.registerBasicCustomer(request));
    }

    @PostMapping("/complete")
    public void completeCustomerAccount(@RequestBody CompletedCustomerRegistrationRequest request){
        registrationService.customerRegistrationToPartial(request);
    }


}
