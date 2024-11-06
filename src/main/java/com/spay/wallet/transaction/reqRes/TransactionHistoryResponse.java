package com.spay.wallet.transaction.reqRes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.CurrencyType;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.PaymentChannel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryResponse {
    private final String transactionId;
    private final String xTransactionId;
    private final PaymentChannel paymentChannel;
    private final TransactionType transactionType;
    private final TransactionStatus status;
    private final String debitAccount;
    private final String debitAccountName;
    private final String creditAccount;
    private final String creditAccountName;
    private final String amount;
    private final CurrencyCode currencyCode;
    private final CurrencyType currencyType;
    private final Boolean isCredit;
    private final String currentBalance;
    private final String chargeFee;
    private final String exchangeRate;
    private final String transactionDateFormat;
    private final LocalDateTime transactionDate;
    private final String description;
    private final String message;
}
