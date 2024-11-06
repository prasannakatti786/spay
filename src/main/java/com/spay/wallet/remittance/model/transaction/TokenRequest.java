package com.spay.wallet.remittance.model.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenRequest {
    private String email;
    private String password;
}
