package com.spay.wallet.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageSenderImplementer  {
    private final Map<String, MessageSender> messageSenders;

    @Async
    public void sendMessage(MessagePayload payload){
        messageSenders.get(payload.getChannel()
                .getChannelName()).sendMessage(payload);
    }
}
