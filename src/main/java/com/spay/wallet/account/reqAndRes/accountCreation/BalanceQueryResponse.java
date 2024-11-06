package com.spay.wallet.account.reqAndRes.accountCreation;

import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
public class BalanceQueryResponse {
    private final String accountNumber;
    private final AccountType accountType;
    private final Double currentBalance;
    private final Double pendingBalance;
    private final Double shadowBalance;
    private final CurrencyType currencyType;
    private final CurrencyCode currencyCode;
    private final String currencyName;
    private final String currencySymbol;
    private final Integer currencyNumericCode;
}
