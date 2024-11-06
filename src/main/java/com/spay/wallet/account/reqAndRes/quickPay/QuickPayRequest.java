package com.spay.wallet.account.reqAndRes.quickPay;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class QuickPayRequest {
    @NotNull(message = "Account required")
    private String account;
}
