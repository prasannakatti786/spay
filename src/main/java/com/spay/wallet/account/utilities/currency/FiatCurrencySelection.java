package com.spay.wallet.account.utilities.currency;


import com.spay.wallet.account.entities.CurrencyCode;

import org.springframework.stereotype.Component;

import java.util.Currency;

@Component("FIAT")
public class FiatCurrencySelection implements CurrencySelection{

    @Override
    public CurrencyInfo getCurrencyInfo(CurrencyCode code) {
        var currency = Currency.getInstance(code.name());
        return new CurrencyInfo(code,currency.getDisplayName(), currency.getSymbol(), currency.getNumericCode(),code.getCurrencyType());
    }
}
