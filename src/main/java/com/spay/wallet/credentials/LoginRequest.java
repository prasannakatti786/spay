package com.spay.wallet.credentials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginRequest {
    private final String username;
    private final String password;
    private final UserType userType;
    private final String emailOtp;
    private final String phoneOtp;
}
