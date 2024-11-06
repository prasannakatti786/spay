package com.spay.wallet.customer.controller;

import com.spay.wallet.account.entities.AccountStatus;
import com.spay.wallet.common.CustomPage;
import com.spay.wallet.customer.reqRes.CustomerAdminDetailResponse;
import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import com.spay.wallet.customer.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@PreAuthorize("hasAuthority('ADMIN') || hasAuthority('DEV')")
@RestController
@RequestMapping("/api/v1/admin-customer")
@RequiredArgsConstructor
public class AdminCustomerController {
    private final CustomerService customerService;

    @GetMapping("/count-customers")
    public ResponseEntity<Map<String,Object>> countCustomers(){
        var totalCustomers= customerService.countCustomers();
        return ResponseEntity.ok(Map.of("totalCustomers",totalCustomers));
    }

    @GetMapping("/customers")
    public ResponseEntity<CustomPage<CustomerDetailResponse>> getCustomers(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(required = false, defaultValue = "30") Integer size){
        return ResponseEntity.ok(customerService.getCustomers(page,size));
    }



    @GetMapping("/search-customer")
    public ResponseEntity<List<CustomerDetailResponse>> getCustomers(@RequestParam String key){
        return ResponseEntity.ok(customerService.searchCustomer(key));
    }

    @GetMapping("/customer-details/{customerId}")
    public ResponseEntity<CustomerAdminDetailResponse> getCustomerDetails(@PathVariable Long customerId){
        return ResponseEntity.ok(customerService.getAdminCustomerDetails(customerId));
    }


    @PutMapping("/enable-or-disable/{customerId}")
    public ResponseEntity<Map<String,String>> enableOrDisableCustomerAccount(@PathVariable Long customerId){
        var isEnabled =  customerService.enableOrDisableCustomerAccount(customerId);
        return ResponseEntity.ok(Map.of("message",
                String.format("Customer account is %s successfully", isEnabled?"enabled":"disabled")));
    }


    @PutMapping("/lock-or-unlock/{customerId}")
    public ResponseEntity<Map<String,String>> unLockOrLockCustomerAccount(@PathVariable Long customerId){
        var isLocked =  customerService.unLockOrLockCustomerAccount(customerId);
        return ResponseEntity.ok(Map.of("message",
                String.format("Customer account is %s successfully", isLocked?"locked":"unlocked")));
    }

}
