package com.spay.wallet.transaction.payment;

import com.spay.wallet.account.entities.CurrencyCode;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentResponse createPayment(PaymentPayload payload);
    PaymentResponse payout(PaymentPayload payload);

    ReceiverInfoResponse getReceiverInfo(String receiverAccount, CurrencyCode currencyCode, BigDecimal amount);

}
