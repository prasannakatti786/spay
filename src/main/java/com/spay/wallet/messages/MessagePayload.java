package com.spay.wallet.messages;

import lombok.*;

@Data
@Builder
public class MessagePayload {
    private final String subject;
    private final String message;
    private final String sendTo;
    private final MessageChannel channel;
}
