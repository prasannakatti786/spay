package com.spay.wallet.account.utilities.currency;

import com.spay.wallet.account.entities.CurrencyCode;

public interface CurrencySelection {
    CurrencyInfo getCurrencyInfo(CurrencyCode code);
}
