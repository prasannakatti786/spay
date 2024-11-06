package com.spay.wallet.remittance.controllers;


import com.spay.wallet.remittance.model.transaction.*;
import com.spay.wallet.remittance.services.*;
//import com.spay.wallet.remittance.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class RemittanceTransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transaction_status/{ptcn_no}")
    public ResponseEntity<
            TransactionResponse> getTransactionStatusByPtcnNo(@PathVariable("ptcn_no") String ptcnNo){
        try {
            return ResponseEntity.ok(transactionService.getTransactionStatusByPtcnNo(ptcnNo));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(TransactionResponse.class));
        }
    }

    @GetMapping("/corr_transaction/{ptcn_no}")
    public ResponseEntity<TransactionResponse> getCorrTransactionByPtcnNo(@PathVariable("ptcn_no") String ptcnNo){
        try {
            return ResponseEntity.ok(transactionService.getCorrTransactionByPtcnNo(ptcnNo));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(TransactionResponse.class));
        }
    }

    @GetMapping("/transactions/products/get_required_fields/{product_id}")
    public ResponseEntity<ProductDetails> getTransactionProductsByProductId(@PathVariable("product_id") Long productId){
        try {
            return ResponseEntity.ok(transactionService.getTransactionProductsByProductId(productId));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(ProductDetails.class));
        }
    }

    @GetMapping("/transactions/date_between")
    public ResponseEntity<TransactionBetweenDatesResponse> getTransactionsDatesBetween(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate){
        try {
            return ResponseEntity.ok(transactionService.getTransactionsDatesBetween(startDate, endDate));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(TransactionBetweenDatesResponse.class));
        }
    }

    @GetMapping("/receiving_transaction/{ptcn_no}")
    public ResponseEntity<ReceivingTransactionResponse> getReceivingTransactionByPtcnNo(@PathVariable("ptcn_no") String ptcnNo){
        try {
            return ResponseEntity.ok(transactionService.getReceivingTransactionByPtcnNo(ptcnNo));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(ReceivingTransactionResponse.class));
        }
    }

    @GetMapping("/transactions/get_charge")
    public ResponseEntity<TransactionChargeResponse> getChargeOfTransaction(@RequestParam("product_code") Long productCode, @RequestParam("destination_country_code") String destinationCountryCode, @RequestParam("local_amount") Long localAmount){
        try {
            return ResponseEntity.ok(transactionService.getChargeOfTransaction(productCode, destinationCountryCode, localAmount));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(TransactionChargeResponse.class));
        }
    }

    @PatchMapping("/approve_transaction/{ptcn_no}")
    public ResponseEntity<ApproveTransactionResponse> approveTransactionByPtcnNo(@PathVariable("ptcn_no") String ptcnNo){
        try {
            return ResponseEntity.ok(transactionService.approveTransactionByPtcnNo(ptcnNo));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(ApproveTransactionResponse.class));
        }
    }

    @PatchMapping("/pay_transaction/{ptcn_no}")
    public ResponseEntity<PayTransactionResponse> payTransactionByPtcnNo(@PathVariable("ptcn_no") String ptcnNo){
        try {
            return ResponseEntity.ok(transactionService.payTransactionByPtcnNo(ptcnNo));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(PayTransactionResponse.class));
        }
    }

    @PostMapping("/send_transaction/")
    public ResponseEntity<SendTransactionResponse> sendTransaction(@RequestBody Transaction transaction){
        try {
            return ResponseEntity.ok(transactionService.sendTransaction(transaction));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(SendTransactionResponse.class));
        }
    }

    @PostMapping("/validate_transaction")
    public ResponseEntity<ValidTransactionResponse> validateTransaction(@RequestBody Transaction transaction){
        try {
            return ResponseEntity.ok(transactionService.validateTransaction(transaction));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(ValidTransactionResponse.class));
        }
    }
}
