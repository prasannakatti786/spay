package com.spay.wallet.customerservice.clients;

public record BasicCustomerRegistrationRequest(String phoneNumber, String email, String credentialId) {
}
