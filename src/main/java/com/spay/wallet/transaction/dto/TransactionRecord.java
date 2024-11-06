package com.spay.wallet.transaction.dto;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.entities.Transaction;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.entities.TransactionLocation;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.entities.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
public class TransactionRecord {
    private final String xTransactionId;
    private final String senderAccount;
    private final String receiverAccount;
    private final String receiverName;
    private final String senderName;
    private final String description;
    private final BigDecimal creditAmount;
    private final BigDecimal debitAmount;
    private final CurrencyCode creditCurrencyCode;
    private final CurrencyCode debitCurrencyCode;
    private final BigDecimal debitCurrentBalance;
    private final BigDecimal creditCurrentBalance;
    private final PaymentChannel paymentChannel;
    private final BigDecimal charge;
    private final BigDecimal exchangeFee;
    private final TransactionStatus transactionStatus;
    private final TransactionType transactionType;
    private final TransactionLocation transactionLocation;
    private final String exchangeMessage;

    public TransactionRecord(Transaction record){
        xTransactionId =  record.getXTransactionId();
        senderAccount =  record.getSenderAccount();
        receiverAccount =  record.getReceiverAccount();
        debitAmount =  record.getDebitAmount();
        creditAmount =  record.getCreditAmount();
        creditCurrencyCode =  record.getCreditCurrencyCode();
        debitCurrencyCode =  record.getDebitCurrencyCode();
        debitCurrentBalance =  record.getDebitCurrentBalance();
        creditCurrentBalance = record.getCreditCurrentBalance();
        description =  record.getDescription();
        paymentChannel =  record.getPaymentChannel();
        exchangeFee =  record.getExchangeFee();
        charge =  record.getCharge();
        transactionStatus =  record.getTransactionStatus();
        transactionLocation = record.getTransactionLocation();
        transactionType = record.getTransactionType();
        receiverName =  record.getReceiverName();
        senderName =  record.getSenderName();
        this.exchangeMessage = record.getExchangeMessage();
    }
}
