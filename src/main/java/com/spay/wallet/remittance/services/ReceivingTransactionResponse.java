package com.spay.wallet.remittance.services;

import com.spay.wallet.remittance.model.ApiResponse;
import com.spay.wallet.remittance.model.transaction.ApiTransaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceivingTransactionResponse {
    private ApiResponse api_response;
    private List<ApiTransaction> transactions;
}
