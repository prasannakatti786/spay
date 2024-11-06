package com.spay.wallet.account.controllers;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.QuickPayAccount;
import com.spay.wallet.account.reqAndRes.quickPay.QuickPayRequest;
import com.spay.wallet.account.services.QuickPayAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

//@PreAuthorize("hasAuthority('CUSTOMER')")
@RestController
@RequestMapping("/api/v1/quick-pay")
@RequiredArgsConstructor
public class QuickPayController {
    private final QuickPayAccountService quickPayAccountService;

    @PostMapping("/add-account/{account}")
    public ResponseEntity<Map<String,String>> addQuickPayAccount(@PathVariable String account, Principal principal){
        var customerId = principal.getName();
        quickPayAccountService.addAccount(new QuickPayRequest(account),customerId);
        return  ResponseEntity.ok(Map.of("message","Account is added quick pay"));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<QuickPayAccount>> getQuickPayAccounts(@RequestParam Integer page, @RequestParam CurrencyCode currencyCode, Principal principal){
        var customerId = principal.getName();
        return  ResponseEntity.ok(quickPayAccountService.getQuickPayAccounts(customerId,currencyCode,page));
    }


    @DeleteMapping("/quickPayAccount")
    public ResponseEntity<Map<String,String>> getQuickPayAccounts(@RequestParam String account, Principal principal){
        var customerId = principal.getName();
        quickPayAccountService.deleteQuickPayAccount(customerId,account);
        return  ResponseEntity.ok(Map.of("message","Account is deleted quick pay"));
    }

}
