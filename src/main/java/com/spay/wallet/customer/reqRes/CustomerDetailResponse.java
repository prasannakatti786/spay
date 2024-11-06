package com.spay.wallet.customer.reqRes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.customer.entities.CustomerLevel;
import com.spay.wallet.customer.entities.Gender;
import com.spay.wallet.customer.entities.UserDocumentType;
import com.spay.wallet.messages.MessageChannel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDetailResponse {
    private final String customerId;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String joinAt;
    private final String phoneNumber;
    private final String email;
    private final String profileImage;
    private final String documentFrontImagePath;
    private final String documentBackImagePath;
    private final String country;
    private final String countryCode;
    private final String city;
    private final String postCode;
    private final String address;
    private final String documentNumber;
    private final UserDocumentType documentType;
    private final CustomerLevel customerLevel;
    private final UserType userType;
    private final Gender gender;
    private final Boolean isEnabledTwoFactorAuth;
    private final MessageChannel messageChannel;
}
