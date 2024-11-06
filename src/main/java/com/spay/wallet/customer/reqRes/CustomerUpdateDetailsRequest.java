package com.spay.wallet.customer.reqRes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.customer.entities.CustomerLevel;
import com.spay.wallet.customer.entities.Gender;
import com.spay.wallet.customer.entities.UserDocumentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerUpdateDetailsRequest {
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String phoneNumber;
    private final String email;
    private final String country;
    private final String countryCode;
    private final String city;
    private final String postCode;
    private final String address;
    private final String documentNumber;
    private final UserDocumentType documentType;
    private final Gender gender;
}
