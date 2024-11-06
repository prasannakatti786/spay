package com.spay.wallet.customerservice.controllers;

import com.spay.wallet.customerservice.clients.CustomerRegistrationResponse;
import com.spay.wallet.customerservice.reqres.CustomerCompleteRegistrationRequest1;
import com.spay.wallet.customerservice.services.CustomerRegistrationService1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer/micro_to_mono_registration")
@RequiredArgsConstructor
public class CustomerRegistrationController1 {
    private final CustomerRegistrationService1 registrationService;

    @PostMapping("/register")
    public ResponseEntity<CustomerRegistrationResponse> registerCustomer(@Valid @RequestBody CustomerCompleteRegistrationRequest1 request){
        var response =  registrationService.completeCustomerRegistrationRequest(request);
        return ResponseEntity.ok(response);
    }

}
