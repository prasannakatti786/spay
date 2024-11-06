package com.spay.wallet.credentials;

import org.apache.commons.lang3.RandomStringUtils;

public class CredentialUtil {
    public static String createOTP(int length){
        String NUMBER = "0123456789";
        return RandomStringUtils.random(length, NUMBER);
    }

}
