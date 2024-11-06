package com.spay.wallet.properies;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wallet")
@RequiredArgsConstructor
@Getter
public class WalletProperties {
    private final Security security;
    private final String accountIdSuffix;
    private final String senderTemplateMessage;
    private final String receiverTemplateMessage;
    private final String labelTemplate;
    private final TwilioProperties twilio;
    private final DevAccount devAccount;
}
