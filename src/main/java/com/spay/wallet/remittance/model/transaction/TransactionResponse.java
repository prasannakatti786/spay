package com.spay.wallet.remittance.model.transaction;

import com.spay.wallet.remittance.model.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionResponse {
    private ApiResponse api_response;
    private ApiTransaction transaction;
}
