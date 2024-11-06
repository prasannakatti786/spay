package com.spay.wallet.account.reqAndRes.accountCreation;

import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.entities.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountCreationRequest {

    @NotNull(message = "Account name id is required")
    @Size(
            message = "Account name must be between 5-40 characters",
            max = 40,
            min = 5
    )
    private final String accountName;
    @NotNull(message = "Account type is required")
    private final AccountType accountType;
    @NotNull(message = "Currency code is required")
    private final CurrencyCode currencyCode;
    @NotNull(message = "A-PIN is required")
    private final String aPin;
}
