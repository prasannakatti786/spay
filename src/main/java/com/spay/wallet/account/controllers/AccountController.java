package com.spay.wallet.account.controllers;


import com.spay.wallet.account.entities.AccountStatus;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountDetailsResponse;
import com.spay.wallet.account.services.AccountService;
import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@PreAuthorize("hasAuthority('CUSTOMER') || hasAuthority('AGENT')")
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/account-details")
    public ResponseEntity<List<AccountDetailsResponse>> getAccounts(Principal principal){
        var customerId = Long.parseLong( principal.getName());
        return ResponseEntity.ok(accountService.getAccounts(customerId));
    }

    @GetMapping("/account-holders")
    public ResponseEntity<List<CustomerDetailResponse>> getCustomersByIds(Principal principal,@RequestParam String account){
        var customerId = principal.getName();
        return ResponseEntity.ok(accountService.getCustomersByIds(customerId,account));
    }


    @PutMapping("/swap-admin-account")
    public ResponseEntity<Map<String,String>> swapAccountAdmin(Principal principal, @RequestParam String accountId, @RequestParam Long toCustomerId){
        var customerId = Long.parseLong( principal.getName());
        accountService.swapAdmin(accountId,customerId,toCustomerId);
        return ResponseEntity.ok(Map.of("message","Account admin role has been assigned to "+toCustomerId));
    }


    @DeleteMapping("/delete-account")
    public ResponseEntity<Map<String,String>> deleteAccount(Principal principal, @RequestParam String accountId){
        var customerId = Long.parseLong( principal.getName());
        accountService.changeAccountStatus(customerId,accountId, AccountStatus.DELETE);
        return ResponseEntity.ok(Map.of("message","Account has been deleted successfully "+accountId));
    }

    @PutMapping("/lost-account")
    public ResponseEntity<Map<String,String>> lostAccount(Principal principal, @RequestParam String accountId){
        var customerId = Long.parseLong( principal.getName());
        accountService.changeAccountStatus(customerId,accountId, AccountStatus.LOST);
        return ResponseEntity.ok(Map.of("message","Account was reported to be lost." + accountId));
    }



}
