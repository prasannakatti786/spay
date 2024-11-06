package com.spay.wallet.properies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TwilioProperties {
    private String accountSID;
    private String authToken;
    private String phoneNumber;
}
