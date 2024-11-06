package com.spay.wallet.customerservice.utilities;

import com.spay.wallet.customerservice.common.security.UserRole;
import com.spay.wallet.customerservice.common.utilities.CustomFormat;
import com.spay.wallet.customerservice.entities.Customer1;
import com.spay.wallet.customerservice.reqres.CustomerDetailResponse1;

public class Mapping1 {
    public static CustomerDetailResponse1 mapCustomerDetail(Customer1 customer) {
        return CustomerDetailResponse1.builder()
                .customerId(customer.getId().toString())
                .middleName(customer.getMiddleName())
                .firstName(customer.getFirstName())
                .email(customer.getEmail())
                .profileImage(customer.getProfilePath())
                .joinAt(CustomFormat.formatDateTime(customer.getJoinAt()))
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .userRole(UserRole.CUSTOMER)
                .credentialId(customer.getCredentialId())
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
                .build();
    }
}
