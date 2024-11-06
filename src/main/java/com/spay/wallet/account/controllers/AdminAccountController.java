package com.spay.wallet.account.controllers;


import com.spay.wallet.account.entities.AccountStatus;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountDetailsResponse;
import com.spay.wallet.account.reqAndRes.accountCreation.BalanceQueryResponse;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.account.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@PreAuthorize("hasAuthority('ADMIN') || hasAuthority('DEV')")
@RestController
@RequestMapping("/api/v1/admin-account")
@RequiredArgsConstructor
public class AdminAccountController {
    private final AccountService accountService;
    private final AccountBalanceService accountBalanceService;

    @GetMapping("/currencies")
    public ResponseEntity<List<CurrencyCode>> getCurrencyCodes(){
        return ResponseEntity.of(Optional.of(Arrays.asList(CurrencyCode.values())));
    }

    @GetMapping("/account-details/{customerId}")
    public ResponseEntity<List<AccountDetailsResponse>> getAccounts(@PathVariable Long customerId){
        return ResponseEntity.ok(accountService.getAccountsByAdmin(customerId));
    }

    @GetMapping("/account-check-balance/{accountNumber}")
    public ResponseEntity<BalanceQueryResponse> balanceQuery(@PathVariable String accountNumber){
        return ResponseEntity.ok(accountBalanceService.adminQueryBalance(accountNumber));
    }

    @PutMapping("/change-account-status/{accountNumber}")
    public ResponseEntity<Map<String,String>> changeAccountStatus(@PathVariable String accountNumber, @RequestParam AccountStatus status){
        accountService.changeAccountStatus(accountNumber, status);
        return ResponseEntity.ok(Map.of("message",String.format("Account %s has been change its status to %s",accountNumber, status.toString())));
    }

    @GetMapping("/total-accounts-balance/{currencyCode}")
    public ResponseEntity<Map<String, Object>> totalAccountBalances(@PathVariable CurrencyCode currencyCode){

        return ResponseEntity.ok(accountBalanceService.getTotalAccountsBalance(currencyCode));
    }




}
