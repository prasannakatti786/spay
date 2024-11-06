package com.spay.wallet.customer.utilities;

import com.spay.wallet.account.utilities.FormatDateAndTime;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import com.spay.wallet.messages.MessageChannel;

public class Mapping {
    public static CustomerDetailResponse mapCustomerDetail(Customer customer) {
        var credential =  customer.getCredential();
        return CustomerDetailResponse.builder()
                .customerId(customer.getId().toString())
                .middleName(customer.getMiddleName())
                .firstName(customer.getFirstName())
                .email(customer.getEmail())
                .profileImage(customer.getProfilePath())
                .joinAt(FormatDateAndTime.formatDateTime(customer.getJoinAt()))
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .userType(credential.getUserType())
                .customerLevel(customer.getCustomerLevel())
                .documentNumber(customer.getDocumentNumber())
                .city(customer.getCity())
                .gender(customer.getGender())
                .country(customer.getCountry())
                .countryCode(customer.getCountryCode())
                .documentType(customer.getDocumentType())
                .postCode(customer.getPostCode())
                .address(customer.getAddress())
                .documentFrontImagePath(customer.getDocumentFrontImagePath())
                .documentBackImagePath(customer.getDocumentBackImagePath())
                .isEnabledTwoFactorAuth(credential.getEnabledTwoFactorAuth() == null?Boolean.FALSE:credential.getEnabledTwoFactorAuth())
                .messageChannel(credential.getMessageChannel() == null? MessageChannel.ALL:credential.getMessageChannel())
                .build();
    }
}
