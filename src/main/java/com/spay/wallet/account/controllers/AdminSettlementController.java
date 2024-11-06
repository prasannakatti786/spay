package com.spay.wallet.account.controllers;


import com.spay.wallet.account.reqAndRes.accountCreation.AccountDetailsResponse;
import com.spay.wallet.account.services.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('ADMIN') || hasAuthority('DEV')")
@RestController
@RequestMapping("/api/v1/admin-settlement")
@RequiredArgsConstructor
public class AdminSettlementController {
    private final SettlementService settlementService;


    @GetMapping("/account")
    public ResponseEntity<AccountDetailsResponse> getSettlementAccount(){
        return ResponseEntity.ok(settlementService.getAccounts());
    }


}
