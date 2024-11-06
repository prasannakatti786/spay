package com.spay.wallet.account.reqAndRes.accountCreation;

import com.spay.wallet.account.entities.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class AccountDetailsResponse {
    private final String customerId;
    private final String accountNumber;
    private final String accountName;
    private final List<AccountHolder> accountHolders;
    private final AccountType accountType;
    private final String createdAt;
    private final AccountStatus accountStatus;
    private final CurrencyType currencyType;
    private final CurrencyCode currencyCode;
    private final String currencyName;
    private final Boolean hasAPin;
}
