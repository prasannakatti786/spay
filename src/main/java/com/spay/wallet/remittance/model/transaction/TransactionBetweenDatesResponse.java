package com.spay.wallet.remittance.model.transaction;

import com.spay.wallet.remittance.model.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TransactionBetweenDatesResponse {
    private ApiResponse api_response;
    private List<ApiTransaction> transactions;
}
