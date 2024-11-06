package com.spay.wallet.credentials;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spay.wallet.messages.MessageChannel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse (String accessToken, String tokenType,
                             Boolean isOtpSent, String message, MessageChannel messageChannel){
}
