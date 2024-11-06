package com.spay.wallet.remittance.model.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerAttributes {
    private int customerType;
    private int gender;
    private String idNo;
    private String idIssueDate;
    private String idExpiryDate;
    private String idIssuedAt;
    private String firstName;
    private String middleName;
    private String lastName;
    private String surname;
    private String primaryCellNumber;
    private String nationalityCode;
    private String primaryAddress;
    private String secondaryAddress;
    private String city;
    private String countryCode;
    private String dateOfBirth;
    private String zip;
    private String email;
    private String pob;
    private String relationToBeneficiary;
    private String occupation;

}