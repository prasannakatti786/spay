package com.spay.wallet.remittance.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ApiConfig {
    private String baseUrl;
    private String email;
    private String password;

}
