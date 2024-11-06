package com.spay.wallet.account.utilities.currency;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.CurrencyType;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CRYPTO")
public class CryptoCurrencySelection  implements CurrencySelection{
    @Override
    public CurrencyInfo getCurrencyInfo(CurrencyCode code) {
        var currency =  currencyCodes().get(code);
        return  new CurrencyInfo(code,currency.b,code.name(),currency.a, CurrencyType.CRYPTO);
    }


    private Map<CurrencyCode, Pair<Integer,String>> currencyCodes(){
        return Map.of(
                CurrencyCode.BTC,
                new Pair<>(100,"BitCoin"),
                CurrencyCode.ETH,
                new Pair<>(101,"Ethereum"),
                CurrencyCode.USDT,
                new Pair<>(102,"Tether USDT (TRC20)"),
                CurrencyCode.USDC,
                new Pair<>(103,"USD Coin"),
                CurrencyCode.XRP,
                new Pair<>(104,"Ripple")
        );
    }
}
