package com.spay.wallet.transaction.reqRes;

import com.spay.wallet.account.entities.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
@Getter
@Builder
public class ExchangeResponse {
    private final String message;
    private final String exchangeRate;
    private final CurrencyCode fromCurrencyCode;
    private final CurrencyCode toCurrencyCode;
    private final String toExchangedAmount;
    private final String fromExchangedAmount;
}
