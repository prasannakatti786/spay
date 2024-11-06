package com.spay.wallet.customerservice.controllers;


import com.spay.wallet.customerservice.common.security.Principal;
import com.spay.wallet.customerservice.reqres.CustomerDetailResponse1;
import com.spay.wallet.customerservice.services.CustomerService1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/micro_customer")
@RequiredArgsConstructor
public class CustomerController1 {
    private final CustomerService1 customerService;
    @GetMapping("/details")
//    @UserAuthorize(roles = {UserRole.ADMIN,UserRole.SUPER_ADMIN,UserRole.CUSTOMER}, permissions = UserPermission.CUSTOMER_DETAIL)
    public ResponseEntity<CustomerDetailResponse1> getDetails(Principal principal){
        var response =  customerService.getCustomerDetails(Long.parseLong(principal.getUserId()));
        return ResponseEntity.ok(response);
    }

}
