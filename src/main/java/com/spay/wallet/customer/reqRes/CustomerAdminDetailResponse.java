package com.spay.wallet.customer.reqRes;

import com.spay.wallet.credentials.UserType;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.entities.CustomerLevel;
import com.spay.wallet.customer.entities.UserDocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CustomerAdminDetailResponse {
    private final Long id;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final LocalDateTime joinAt;
    private final String country;
    private final String countryCode;
    private final String city;
    private final String postCode;
    private final String address;
    private final String profilePath;
    private final String faceImagePath;
    private final String documentNumber;
    private final String referralCode;
    private final UserDocumentType documentType;
    private final String phoneNumber;
    private final CustomerLevel customerLevel;
    private final String email;
    private final UserType userType;
    private final Boolean isEnabled;
    private final Boolean isLocked;

    public CustomerAdminDetailResponse(Customer customer){
        id = customer.getId();
        firstName = customer.getFirstName();
        middleName =  customer.getMiddleName();
        lastName =  customer.getLastName();
        dateOfBirth = customer.getDateOfBirth();
        joinAt =  customer.getJoinAt();
        country =  customer.getCountry();
        countryCode =  customer.getCountryCode();
        city =  customer.getCity();
        postCode = customer.getPostCode();
        address =  customer.getAddress();
        profilePath =  customer.getProfilePath();
        faceImagePath =  customer.getFaceImagePath();
        documentNumber =  customer.getDocumentNumber();
        referralCode =  customer.getReferralCode();
        documentType =  customer.getDocumentType();
        phoneNumber =  customer.getPhoneNumber();
        customerLevel =  customer.getCustomerLevel();
        email =  customer.getEmail();
        var credential =  customer.getCredential();
        userType =  credential.getUserType();
        isEnabled =  credential.getIsEnabled();
        isLocked=  credential.getIsLocked();
    }
}
