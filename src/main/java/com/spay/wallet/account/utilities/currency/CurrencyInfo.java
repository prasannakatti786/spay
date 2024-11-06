package com.spay.wallet.account.utilities.currency;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CurrencyInfo {
    private final CurrencyCode code;
    private final String name;
    private final String symbol;
    private final Integer numericCode;
    private final CurrencyType type;
}
