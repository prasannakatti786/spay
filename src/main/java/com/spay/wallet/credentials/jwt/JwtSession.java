package com.spay.wallet.credentials.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtSession {
    private final Long credentialId;
    private final String sessionId;
}
