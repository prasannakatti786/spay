package com.spay.wallet.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageChannel {
    SMS("smsSenderChannel"),
    EMAIL("emailSenderChannel"),
    WHATSAPP("whatsAppSenderChannel"),
    ALL("all");
    private final String channelName;
}
