package com.spay.wallet.transaction.reqRes;


import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.entities.TransactionLocation;
import com.spay.wallet.transaction.entities.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class TransactionRequest {
    @NotNull(message = "Sender Account  is required")
    private final String senderAccount;
    @NotNull(message = "Receiver Account  is required")
    private final String receiverAccount;
    @NotNull(message = "Currency type  is required")
    private final CurrencyCode currencyCode;
    @NotNull(message = "Amount is required")
    private final BigDecimal amount;
    @NotNull(message = "Payment method is required")
    private final PaymentChannel paymentChannel;
    @NotNull(message = "Transaction type is required")
    private final TransactionType transactionType;
    @NotNull(message = "APIN is required")
    private final String aPin;
    @NotNull(message = "Location is required")
    private final TransactionLocation location;
    @NotNull(message = "Description is required")
    private final String description;
}
