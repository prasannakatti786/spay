package com.spay.wallet.transaction.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spay.wallet.account.entities.CurrencyCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiverInfoResponse {
    private final String receiverAccount;
    private final String receiverAccountName;
    private final String receiverCustomerName;
    private final String receiverCustomerId;
    private final String receiverProfile;
    private final String exchangeRate;
    private final CurrencyCode receiverCurrencyCode;
}
