package com.spay.wallet.messages.sms;


import com.spay.wallet.messages.MessagePayload;
import com.spay.wallet.messages.MessageSender;
import com.spay.wallet.properies.WalletProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("smsSenderChannel")
@RequiredArgsConstructor
@Slf4j
public class SmsMessageSenderChannel implements MessageSender {
    private final WalletProperties walletProperties;

    @Override
    public void sendMessage(MessagePayload payload) {
//        var twilioProperties =  walletProperties.getTwilio();
//        Twilio.init(twilioProperties.getAccountSID(), twilioProperties.getAuthToken());
//        Message message = Message.creator(
//                        new com.twilio.type.PhoneNumber(StringUtils.trimAllWhitespace(payload.getSendTo())),
//                        new com.twilio.type.PhoneNumber(StringUtils.trimAllWhitespace(twilioProperties.getPhoneNumber())),
//                        payload.getMessage())
//                .create();

        log.info("SMS Message is been sent to {}",payload);
    }
}
