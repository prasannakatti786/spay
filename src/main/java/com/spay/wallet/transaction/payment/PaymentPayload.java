package com.spay.wallet.transaction.payment;


import com.spay.wallet.transaction.entities.TransactionLocation;
import com.spay.wallet.transaction.entities.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class PaymentPayload {
    private final String senderAccount;
    private final String receiverAccount;
    private final String currencyCode;
    private final BigDecimal amount;
    private final PaymentChannel paymentChannel;
    private final String aPin;
    private final TransactionLocation location;
    private final String description;
    private final TransactionType transactionType;
}
