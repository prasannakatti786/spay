package com.spay.wallet.customer.reqRes;

import com.spay.wallet.customer.entities.UserDocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerRegistrationRequest(
        @NotNull(message = "Please enter your first name")
        @Size(min = 3, max = 50, message = "First Name Min is 3 characters and Max is 50 characters")
        String firstName,
        @NotNull(message = "Please enter your middle name")
        @Size(min = 1, max = 50, message = "Middle Name Min is 1 characters and Max is 50 characters")
        String middleName,
        String lastName,
        @NotNull(message = "Please enter your country")
        @Size(min = 3, max = 40, message = "Country Name Min is 3 characters and Max is 40 characters")
        String country,
        @NotNull(message = "Please enter your code")
        @Size(min = 2, max = 5, message = "Country code Min is 2 characters and Max is 5 characters")
        String countryCode,
        @NotNull(message = "Please enter your phone number")
        @Size(min = 13, max = 13, message = "Phone number Min is 13 characters and Max is 13 characters")
        String phoneNumber,
        @NotNull(message = "Please enter your email")
        @Size(min = 5, max = 50, message = "Email address Min is 5 characters and Max is 50 characters")
        @Email(message = "Email must be correct email address")
        String email,
        UserDocumentType documentType,
        String documentNumber

) {


}
