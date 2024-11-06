package com.spay.wallet.account.utilities.currency;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.CurrencyType;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CREDIT")
public class CreditCurrencySelection implements CurrencySelection{
    @Override
    public CurrencyInfo getCurrencyInfo(CurrencyCode code) {
        var currency =  currencyCodes().get(code);
        return  new CurrencyInfo(code,currency.b,code.name(),currency.a, CurrencyType.CRYPTO);
    }


    private Map<CurrencyCode, Pair<Integer,String>> currencyCodes(){
        return Map.of(
                CurrencyCode.SPAY,
                new Pair<>(400,"Spay Coin")
        );
    }
}
