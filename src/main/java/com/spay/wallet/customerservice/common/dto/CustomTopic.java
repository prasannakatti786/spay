package com.spay.wallet.customerservice.common.dto;

import lombok.Getter;


@Getter
public class CustomTopic {
   public final static String NOTIFICATION_OTP ="notification-otp-topic";
   public final static String REGENERATE_OTP ="regenerate-otp-topic";
   public final static String CHANGE_USER_NOTIFICATION ="change-user-notification-topic";
   public final static String DEFAULT_ACCOUNT_CREATION ="default-account-creation-topic";
   public final static String CREATE_TRANSACTION_RECORD="create-transaction-record-topic";
}
