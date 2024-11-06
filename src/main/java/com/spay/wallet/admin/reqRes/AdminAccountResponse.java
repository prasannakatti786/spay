package com.spay.wallet.admin.reqRes;

import com.spay.wallet.credentials.UserType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class AdminAccountResponse {
    private final UUID id;
    private final String fullName;
    private final String email;
    private final UserType userType;
}
