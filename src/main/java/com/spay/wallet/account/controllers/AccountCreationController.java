package com.spay.wallet.account.controllers;


import com.spay.wallet.account.reqAndRes.accountCreation.AccountCreationRequest;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountCreationResponse;
import com.spay.wallet.account.services.AccountCreationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@PreAuthorize("hasAuthority('CUSTOMER') || hasAuthority('AGENT')")
@RestController
@RequestMapping("/api/v1/account-creation")
@RequiredArgsConstructor
public class AccountCreationController {
    private final AccountCreationService accountCreationService;

    @PutMapping("/change-aPin")
    public ResponseEntity<Map<String,String>> changeAPIN(Principal principal, @RequestParam String accountId,@RequestParam String oldAPin,  @RequestParam String aPin){
        var customerId = principal.getName();
        accountCreationService.changeAPin(customerId,accountId,aPin, oldAPin);
        return ResponseEntity.ok(Map.of("message","A-PIN has been changed successfully "+accountId));
    }

    @PostMapping("/add-new-account-holder")
    public ResponseEntity<AccountCreationResponse> addNewAccountHolder(@RequestParam String accountNumber,@RequestParam Long joinerId, Principal principal){
        var customerId = Long.parseLong(principal.getName());
        return ResponseEntity.ok(accountCreationService.addNewAccountHolderToJoinAccount(customerId,accountNumber,joinerId));
    }


}
