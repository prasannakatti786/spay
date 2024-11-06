package com.spay.wallet.transaction.payment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.spay.wallet.transaction.reqRes.TransactionHistoryResponse;
import com.spay.wallet.transaction.reqRes.TransactionRecordResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private final String transactionId;
    private final String currencyCode;
    private final BigDecimal amount;
    private final TransactionHistoryResponse transactionRecordResponse;
}
