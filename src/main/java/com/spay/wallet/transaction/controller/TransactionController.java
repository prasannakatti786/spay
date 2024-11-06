package com.spay.wallet.transaction.controller;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.dto.ChargeResult;
import com.spay.wallet.transaction.dto.ExchangeResult;
import com.spay.wallet.transaction.entities.ChargeRate;
import com.spay.wallet.transaction.entities.TopUpWaitingTransaction;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.payment.PaymentPayload;
import com.spay.wallet.transaction.payment.PaymentResponse;
import com.spay.wallet.transaction.payment.ReceiverInfoResponse;
import com.spay.wallet.transaction.payment.paypal.PaypalPaymentService;
import com.spay.wallet.transaction.reqRes.*;
import com.spay.wallet.transaction.service.TopWaitingTransactionService;
import com.spay.wallet.transaction.service.TransactionRecordService;
import com.spay.wallet.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final PaypalPaymentService paypalPaymentService;
    private final TransactionService transactionService;
    private final TransactionRecordService transactionRecordService;
    private final TopWaitingTransactionService topWaitingTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> transferMoney(@Valid @RequestBody TransactionRequest request, Principal principal){
        var customerId = principal.getName();
        return ResponseEntity.ok(transactionService.makePayment(request,customerId));
    }

    @GetMapping("/exchange-rate")
    public ResponseEntity<ExchangeResponse> getExchangeResult(@RequestParam BigDecimal amount, @RequestParam CurrencyCode fromCurrencyCode,
                                                              @RequestParam CurrencyCode toCurrencyCode){
        return ResponseEntity.ok(transactionService.getExchangeRate(amount,fromCurrencyCode,toCurrencyCode));
    }

    @GetMapping("/charge-fee")
    public ResponseEntity<ChargeResult> getChargeFee(@RequestParam BigDecimal amount, @RequestParam PaymentChannel method,
                                                     @RequestParam TransactionType type, @RequestParam String countryCode){
        return ResponseEntity.ok(transactionService.getChargeFee(amount,method,type,countryCode));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping("/top-up-waiting")
    public ResponseEntity<Map<String, String>> topUpWaiting(@RequestBody TopUpWaitingTransaction transaction){
        var secret =topWaitingTransactionService.createTopUpWaitingTransaction(transaction);
        return ResponseEntity.ok(Map.of("secret",secret));
    }

    @PreAuthorize("hasAuthority('AGENT')")
    @GetMapping("/withdrawal-requests/{accountId}")
    public ResponseEntity<List<TransactionHistoryResponse>> getWithdrawalRequests(@PathVariable String accountId, Principal principal){
        var agentUserId = principal.getName();
        return ResponseEntity.ok(transactionRecordService.getPendingTransactions(accountId,agentUserId));
    }

    @PreAuthorize("hasAuthority('AGENT')")
    @PostMapping("/withdrawal-acceptance")
    public ResponseEntity<PaymentResponse> acceptanceWithdrawal(@RequestBody AgentWithdrawalAcceptanceRequest request, Principal principal){
        var agentUserId = principal.getName();
        return ResponseEntity.ok(transactionService.agentWithdrawalAcceptance(request,agentUserId));
    }


    @GetMapping("/miniStatement/{accountId}")
    public ResponseEntity<List<TransactionHistoryResponse>> getMiniStatement( @PathVariable String accountId, Principal principal){
        var customerId = principal.getName();

        return ResponseEntity.ok(transactionRecordService.miniStatement(accountId,customerId));
    }

    @GetMapping("/fullStatementByDate/{accountId}/{start}/{end}")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionByDate(@PathVariable String accountId, @PathVariable  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start, @PathVariable  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end, Principal principal){
        var customerId = principal.getName();
        return ResponseEntity.ok(transactionRecordService.getTransactionByDate(accountId,customerId,start,end));
    }

    @GetMapping("/fullStatementByDateSendEmail/{accountId}/{start}/{end}")
    public ResponseEntity<Map<String, String>> getTransactionByDateByEmail(@PathVariable String accountId, @PathVariable  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start, @PathVariable  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end, Principal principal){
        var customerId = principal.getName();
        transactionRecordService.getTransactionByDateAndSendEmail(accountId,customerId,start,end);
        return ResponseEntity.ok(Map.of("message","We sent your activities to your email."));
    }


    @GetMapping("/fullStatementByDateByAccount/{accountId}/{start}/{end}/{otherAccount}")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionByDateByAccount(@PathVariable String accountId, @PathVariable  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                                                          @PathVariable  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                                                                          @PathVariable String otherAccount,
                                                                                          Principal principal){
        var customerId = principal.getName();
        return ResponseEntity.ok(transactionRecordService.getTransactionByDateAndAccount(accountId,customerId,otherAccount,start,end));
    }

    @PostMapping("/receiver-info")
    public ResponseEntity<ReceiverInfoResponse> getReceiverInfo(@Valid @RequestBody ReceiverInfoRequest request){
        return ResponseEntity.ok(transactionService.getReceiverInfo(request));
    }

    @PostMapping("/create-paypal")
    public PaymentResponse testPayment(@RequestBody PaymentPayload payload) {
        return paypalPaymentService.createPayment(payload);
    }

    @GetMapping("/paypal-payment-cancel")
    public void paypalPaymentCancel(){
        System.out.println("Paypal payment cancelled");
    }

    @GetMapping("/paypal-payment-successs")
    public Object paypalPaymentSuccesss(@RequestParam("PayerID") String payerId, @RequestParam("paymentId") String paymentId, @RequestParam("token") String token){
        System.out.println(token);
        return null;
    }
    
    @GetMapping("/paypal-payment-success")
    public String paypalPaymentSuccess(@RequestParam("PayerID") String payerId, @RequestParam("paymentId") String paymentId, @RequestParam("token") String token){
        System.out.println(token);
       return  null;
        // return paypalPaymentService.completePayment(paymentId,payerId).toJSON();
    }

}
