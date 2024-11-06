package com.spay.wallet.messages.email;

import com.spay.wallet.messages.MessagePayload;
import com.spay.wallet.messages.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailSenderChannel")
@RequiredArgsConstructor
public class EmailMessageSenderChannel implements MessageSender {
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void sendMessage(MessagePayload payload) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(payload.getSendTo());
        message.setSubject(applicationName+" "+payload.getSubject());
        message.setText(payload.getMessage());
        emailSender.send(message);
    }
}
