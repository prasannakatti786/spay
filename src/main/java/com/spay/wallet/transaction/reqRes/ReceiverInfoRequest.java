package com.spay.wallet.transaction.reqRes;


import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.entities.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReceiverInfoRequest {
    @NotNull(message = "Receiver Account is required")
    private final String receiverAccount;
    @NotNull(message = "Transaction type is required")
    private final PaymentChannel paymentChannel;
    @NotNull(message = "Transaction type is required")
    private final TransactionType transactionType;
}
