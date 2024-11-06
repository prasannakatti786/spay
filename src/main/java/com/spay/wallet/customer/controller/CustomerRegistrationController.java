package com.spay.wallet.customer.controller;

import com.spay.wallet.credentials.LoginRequest;
import com.spay.wallet.credentials.TokenResponse;
import com.spay.wallet.customer.reqRes.CustomerCreatePasswordRequest;
import com.spay.wallet.customer.reqRes.CustomerRegistrationRequest;
import com.spay.wallet.customer.reqRes.CustomerRegistrationResponse;
import com.spay.wallet.customer.services.CustomerRegistrationService;
import com.spay.wallet.customerservice.clients.BasicCustomerRegistrationRequest;
import com.spay.wallet.customerservice.services.CustomerRegistrationService1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer-registration")
@RequiredArgsConstructor
public class CustomerRegistrationController {
    private final CustomerRegistrationService customerRegistrationService;
    private final CustomerRegistrationService1 registrationService;

    @PostMapping("/create")
    public ResponseEntity<CustomerRegistrationResponse> registerNewCustomer(@Valid @RequestBody CustomerRegistrationRequest request){
        var response =  customerRegistrationService.registerNewCustomer(request);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/complete-agent-registration")
    public ResponseEntity<CustomerRegistrationResponse> completeAgentRegistration(@Valid @RequestBody CustomerRegistrationRequest request){
        var response =  customerRegistrationService.completeAgentRegistration(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify-otp/{customerId}")
    public ResponseEntity<CustomerRegistrationResponse> verifyOtp(@RequestParam String emailOtp,
                                                                  @RequestParam String phoneOtp,
                                                                  @PathVariable Long customerId){
        var response =  customerRegistrationService.validateOtp(emailOtp,phoneOtp,customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-password")
    public ResponseEntity<TokenResponse> createPassword(@Valid @RequestBody CustomerCreatePasswordRequest request){
        var response =  customerRegistrationService.createPassword(request.getCustomerId(),
                request.getPassword(), request.getEmailOtp(), request.getPhoneOtp());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<CustomerRegistrationResponse> forgotPassword(@RequestBody LoginRequest request){
        var response =  customerRegistrationService.forgotPasswordByEmailOrPhoneNumber(request.getUsername());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/resend-otp-email/{customerId}")
    public ResponseEntity<CustomerRegistrationResponse> resendOtpEmail(@PathVariable Long customerId){
       customerRegistrationService.resendOtpByEmail(customerId);
       return ResponseEntity.ok(new CustomerRegistrationResponse("Email otp has bean sent again",customerId));
    }

    @PostMapping("/resend-otp-phone/{customerId}")
    public ResponseEntity<CustomerRegistrationResponse> resendOtpPhone(@PathVariable Long customerId){
        customerRegistrationService.resendOtpByPhone(customerId);
        return ResponseEntity.ok(new CustomerRegistrationResponse("Phone otp has bean sent again",customerId));
    }


}
