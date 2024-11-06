package com.spay.wallet.transaction.controller;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.entities.*;
import com.spay.wallet.transaction.reqRes.*;
import com.spay.wallet.transaction.service.ChargeAndExchangeService;
import com.spay.wallet.transaction.service.TopWaitingTransactionService;
import com.spay.wallet.transaction.service.TransactionRecordService;
import com.spay.wallet.common.CustomPage;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PreAuthorize("hasAuthority('ADMIN') || hasAuthority('DEV')")
@RestController
@RequestMapping("/api/v1/admin-transaction")
@RequiredArgsConstructor
public class AdminTransactionController {
    private final TransactionRecordService transactionRecordService;
    private final ChargeAndExchangeService chargeAndExchangeService;
    private final TopWaitingTransactionService topWaitingTransactionService;

    @GetMapping("/total-transactions")
    public ResponseEntity<TransactionTypeCountResponse> countTransaction(){
        return ResponseEntity.ok(transactionRecordService.countTransaction());
    }

    @GetMapping("/profits-calculate")
    public ResponseEntity<ProfitByCategoryResponse> calculateProfits(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end, @RequestParam(required = false) CurrencyCode currencyCode){
        return ResponseEntity.ok(transactionRecordService.getProfitsByCategory(start,end,currencyCode == null?CurrencyCode.USD:currencyCode));
    }

    @GetMapping("/amounts-calculate/{type}")
    public ResponseEntity<TransactionAmountsCategoryResponse> calculateAmounts(@PathVariable String type,
                                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss") LocalDateTime start,
                                                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss") LocalDateTime end,
                                                                               @RequestParam(required = false) CurrencyCode currencyCode){
        return ResponseEntity.ok(transactionRecordService.transactionAmountsCategoryResponse(start,end,currencyCode == null?CurrencyCode.USD:currencyCode,type));
    }

    @GetMapping("/transactions")
    public ResponseEntity<CustomPage<Transaction>> getTransactions(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                   @RequestParam(required = false, defaultValue = "30") Integer size,
                                                                   @RequestParam(required = false) TransactionType type){
        return ResponseEntity.ok(transactionRecordService.getTransactions(page,size, type));
    }

    @GetMapping("/settlement-transactions")
    public ResponseEntity<CustomPage<Transaction>> getSettlement (@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                   @RequestParam(required = false, defaultValue = "30") Integer size){
        return ResponseEntity.ok(transactionRecordService.getSettlementTransactions(page,size));
    }

    @GetMapping("/transactions/{account}")
    public ResponseEntity<CustomPage<Transaction>> getTransactionsByAccount(@PathVariable String account,@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                   @RequestParam(required = false, defaultValue = "30") Integer size,
                                                                   @RequestParam(required = false) TransactionType type){
        return ResponseEntity.ok(transactionRecordService.getAccountTransactions(page,size, type,account));
    }

    @GetMapping("/search-transaction")
    public ResponseEntity<List<Transaction>> searchTransaction(@RequestParam String key){
        return ResponseEntity.ok(transactionRecordService.searchTransaction(key));
    }

    @GetMapping("/charge-fees")
    public ResponseEntity<List<ChargeRate>> getChargeRate(){
        return ResponseEntity.ok(chargeAndExchangeService.getCharges());
    }

    @PostMapping("/add-charge-fee")
    public ResponseEntity<ChargeRate> addNewChargeFee(@RequestBody ChargeRate chargeRate){
        return ResponseEntity.ok(chargeAndExchangeService.addChargeRate(chargeRate.getPaymentChannel(),chargeRate.getTransactionType(),chargeRate.getCharge()));
    }

    @PutMapping("/change-charge-fee")
    public ResponseEntity<ChargeRate> changeChargeFee(@RequestParam UUID id, @RequestParam BigDecimal fee){
        return ResponseEntity.ok(chargeAndExchangeService.updateChargeFee(id,fee));
    }

    @DeleteMapping("/charge-fee")
    public ResponseEntity<Map<String, String>> deleteChargeFee(@RequestParam UUID id){
        chargeAndExchangeService.deleteChargeFee(id);
        return ResponseEntity.ok(Map.of("message","Charge fee is deleted successfully"));
    }

    @GetMapping("/exchange-rates")
    public ResponseEntity<List<ExchangeRate>> getExchangeRates(){
        return ResponseEntity.ok(chargeAndExchangeService.getExchangeRates());
    }

    @PostMapping("/add-exchange-rate")
    public ResponseEntity<ExchangeRate> addNewExchangeRate(@RequestBody ExchangeRate exchangeRate){
        return ResponseEntity.ok(chargeAndExchangeService.addExchangeRate(exchangeRate.getFromCurrencyCode(),
                exchangeRate.getFromExchangeRate(),exchangeRate.getToCurrencyCode(),exchangeRate.getToExchangeRate()));
    }

    @PutMapping("/change-exchange-rate")
    public ResponseEntity<ExchangeRate> changeExchangeRate(@RequestParam UUID id, @RequestParam BigDecimal fromRate,@RequestParam BigDecimal toRate){
        return ResponseEntity.ok(chargeAndExchangeService.updateExchangeRate(id,fromRate,toRate));
    }

    @DeleteMapping("/exchange-rate")
    public ResponseEntity<Map<String, String>> deleteExchangeRate(@RequestParam UUID id){
        chargeAndExchangeService.deleteExchangeRate(id);
        return ResponseEntity.ok(Map.of("message","Exchange rate is deleted successfully"));
    }

    @GetMapping("/top-up-waiting/{accountId}")
    public ResponseEntity<List<TopUpWaitingTransaction>> getTopUps(@PathVariable String accountId){
        return ResponseEntity.ok(topWaitingTransactionService.getTopUps(accountId));
    }

    @PostMapping("/top-up-customer")
    public ResponseEntity<Map<String,Object>> topUpCustomer(Principal principal,@RequestParam Long id, @RequestParam String aPin,@RequestParam Double amount){
        topWaitingTransactionService.topUpAccount(id,principal.getName(),aPin,amount);
        return ResponseEntity.ok(Map.of("message","Customer account top up successfully"));
    }



}
