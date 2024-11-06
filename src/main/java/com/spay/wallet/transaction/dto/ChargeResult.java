package com.spay.wallet.transaction.dto;

import java.math.BigDecimal;

public record ChargeResult(
        BigDecimal amount, BigDecimal charge,
        BigDecimal amountAndCharge){

}