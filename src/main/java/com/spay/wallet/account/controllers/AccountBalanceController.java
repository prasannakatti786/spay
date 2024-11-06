package com.spay.wallet.account.controllers;


import com.spay.wallet.account.entities.AccountStatus;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountDetailsResponse;
import com.spay.wallet.account.reqAndRes.accountCreation.BalanceQueryResponse;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.account.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@PreAuthorize("hasAuthority('CUSTOMER') || hasAuthority('AGENT')")
@RestController
@RequestMapping("/api/v1/account-balance")
@RequiredArgsConstructor

public class AccountBalanceController {
    private final AccountBalanceService accountService;

    @GetMapping("/balance-query/{account}")
    public ResponseEntity<BalanceQueryResponse> balanceQuery(Principal principal, @PathVariable String account){
        var customerId = Long.parseLong( principal.getName());
        return ResponseEntity.ok(accountService.queryBalance(customerId,account));
    }

    @GetMapping("/balance-query-all")
    public ResponseEntity<List<BalanceQueryResponse>> queryAllBalances(Principal principal){
        var customerId = Long.parseLong( principal.getName());
        return ResponseEntity.ok(accountService.queryBalances(customerId));
    }



}
