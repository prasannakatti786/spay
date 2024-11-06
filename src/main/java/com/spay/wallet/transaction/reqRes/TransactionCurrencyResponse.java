package com.spay.wallet.transaction.reqRes;

import com.spay.wallet.account.entities.CurrencyCode;
import lombok.Data;

@Data
public class TransactionCurrencyResponse {
    private final CurrencyCode[] currencyCodes = CurrencyCode.values();
    private final CurrencyCode currencyCode;
}
