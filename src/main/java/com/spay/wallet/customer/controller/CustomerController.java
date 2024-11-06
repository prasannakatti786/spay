package com.spay.wallet.customer.controller;

import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import com.spay.wallet.customer.reqRes.CustomerUpdateDetailsRequest;
import com.spay.wallet.customer.services.CustomerRegistrationService;
import com.spay.wallet.customer.services.CustomerService;
import com.spay.wallet.messages.MessageChannel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@PreAuthorize("hasAuthority('CUSTOMER') || hasAuthority('AGENT')")
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerRegistrationService customerRegistrationService;
    private final CustomerService customerService;
    @PutMapping("/change-details")
    public String updateDetails(){
        return "Details has been updated";
    }

    @GetMapping("/customer-detail")
    public ResponseEntity<CustomerDetailResponse> getCustomerInfo(Principal principal){
        return ResponseEntity.ok(customerService.getCustomerDetails(Long.parseLong(principal.getName())));
    }



    @PutMapping("/upload-profile")
    public ResponseEntity<String> uploadProfile(Principal principal, @RequestParam MultipartFile profile) {
        var id = principal.getName();
        customerRegistrationService.uploadProfileImage(id, profile);
        return ResponseEntity.ok("Profile is uploaded successfully");
    }

    @PutMapping("/update-user-details")
    public ResponseEntity<Map<String, Object>> updateUserDetails(Principal principal, @RequestBody CustomerUpdateDetailsRequest request){
        var id =Long.parseLong(principal.getName());
        var data = customerRegistrationService.updateCustomerDetails(id,request);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/verify-email-or-phone-otp")
    public ResponseEntity<Map<String, Object>> verifyPhoneOrEmailChanges(Principal principal, @RequestParam(required = false) String phoneOtp,@RequestParam(required = false) String emailOtp){
        var id =Long.parseLong(principal.getName());
        var response = customerRegistrationService.verifyPhoneNumberOrEmailUpdate(id,phoneOtp,emailOtp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email-or-phone-otp-complete")
    public ResponseEntity<Map<String, Object>> verifyPhoneOrEmailChangesComplete(Principal principal, @RequestParam(required = false) String phoneOtp,@RequestParam(required = false) String emailOtp){
        var id =Long.parseLong(principal.getName());
        customerRegistrationService.verifyPhoneNumberOrEmailUpdateComplete(id,phoneOtp,emailOtp);
        return ResponseEntity.ok(Map.of("message","Email or phone number is changed successfully"));
    }

    @PutMapping("/enable-or-disable/two-factor-auth")
    public ResponseEntity<Map<String, Object>> enableOrDisableTwoFacterAuth(Principal principal){
        var id =Long.parseLong(principal.getName());
        var isEnabled = customerRegistrationService.enableOrDisableTwoFactorAuth(id);
        return ResponseEntity.ok(Map.of("message","Two factor auth %s successfully".formatted(isEnabled?"enabled":"disabled")));
    }

    @PutMapping("/message-channel")
    public ResponseEntity<Map<String, Object>> changeMessageChannel(Principal principal, @RequestParam MessageChannel channel){
        var id =Long.parseLong(principal.getName());
         customerRegistrationService.changeMessageChannelOtpChannel(id,channel);
        return ResponseEntity.ok(Map.of("message","message channel is changed successfully"));
    }

}
