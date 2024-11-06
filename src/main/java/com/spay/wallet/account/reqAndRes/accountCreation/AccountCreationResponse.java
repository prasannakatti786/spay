package com.spay.wallet.account.reqAndRes.accountCreation;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountCreationResponse {
    private final String message;
    private final String accountNumber;
    private final String accountHolder;
    private final String accountName;
}
