package com.spay.wallet.customerservice.reqres;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.spay.wallet.customerservice.common.security.UserRole;
import com.spay.wallet.customerservice.entities.CustomerDocumentType;
import com.spay.wallet.customerservice.entities.CustomerLevel1;
import com.spay.wallet.customerservice.entities.Gender1;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDetailResponse1 {
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
    private final CustomerDocumentType documentType;
    private final CustomerLevel1 customerLevel;
    private final UserRole userRole;
    private final Gender1 gender;
    private final String credentialId;
}
