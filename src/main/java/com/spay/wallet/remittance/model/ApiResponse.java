package com.spay.wallet.remittance.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse {
    private int status;
    private int status_code;
    private String message;
    private Date time_stamp;
}
