package com.spay.wallet.transaction.dto;

import com.spay.wallet.account.entities.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class ExchangeResult {
    private final String message;
    private final BigDecimal exchangeRate;
    private final CurrencyCode fromCurrencyCode;
    private final CurrencyCode toCurrencyCode;
    private final BigDecimal toExchangedAmount;
    private final BigDecimal fromExchangedAmount;

}
