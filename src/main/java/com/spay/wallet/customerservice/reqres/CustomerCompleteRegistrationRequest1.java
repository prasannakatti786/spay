package com.spay.wallet.customerservice.reqres;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CustomerCompleteRegistrationRequest1(
    @NotNull(message = "Please enter your first name")
    @Size(min = 3, max = 50, message = "First Name Min is 3 characters and Max is 50 characters")
    String firstName,
    String middleName,
    @NotNull(message = "Please enter your last name")
    @Size(min = 1, max = 50, message = "Last Name Min is 1 characters and Max is 50 characters")
    String lastName,
    @NotNull(message = "Please enter your country")
    @Size(min = 3, max = 40, message = "Country Name Min is 3 characters and Max is 40 characters")
    String country,
    @NotNull(message = "Please enter your country code")
    @Size(min = 2, max = 5, message = "Country code Min is 2 characters and Max is 5 characters")
    String countryCode,
    @NotNull(message = "Date of birth")
    LocalDate dateOfBirth,
    @NotNull(message = "Customer Id is required")
    String customerId,
    @NotNull(message = "Credential Id is required")
    String credentialId,
    String referralCode


    )
{
}
