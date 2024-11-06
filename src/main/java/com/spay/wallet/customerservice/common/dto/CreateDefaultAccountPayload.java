package com.spay.wallet.customerservice.common.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class CreateDefaultAccountPayload {
    private String accountName;
    private String userType;
    private String msisdn;
    private String aPin;
    private String userId;
}
