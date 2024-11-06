package com.spay.wallet.account.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CurrencyCode {
    SOS(CurrencyType.FIAT),
    USD(CurrencyType.FIAT),
    GBP(CurrencyType.FIAT),
    ETB(CurrencyType.FIAT),
    EUR(CurrencyType.FIAT),
    KES(CurrencyType.FIAT),
    TRL(CurrencyType.FIAT),
    INR(CurrencyType.FIAT),
    USDT(CurrencyType.CRYPTO),
    USDC(CurrencyType.CRYPTO),
    BTC(CurrencyType.CRYPTO),
    ETH(CurrencyType.CRYPTO),
    XRP(CurrencyType.CRYPTO),
    SPAY(CurrencyType.CREDIT);
    private final CurrencyType currencyType;

    public Boolean checkCurrencyType(CurrencyType currencyType,CurrencyCode currencyCode){
        return currencyCode.getCurrencyType().equals(currencyType);
    }
}