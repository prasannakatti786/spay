package com.spay.wallet.remittance.model.transaction;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductApiResponse {
    private Integer status_code;
    private String message;
    private Long timestamp;
    private TransferDetails transfer_to;
}
