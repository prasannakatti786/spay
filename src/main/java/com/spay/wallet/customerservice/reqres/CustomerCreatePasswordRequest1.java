package com.spay.wallet.customerservice.reqres;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomerCreatePasswordRequest1 {
    @NotNull(message = "Password can not be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#@$!%*?&])[A-Za-z\\d#@$!%*?&]{8,}$",message = "Password Requirements:\n" +
            "- At least 8 characters long\n" +
            "- Less the 50 characters long\n" +
            "- Must contain at least one uppercase letter\n" +
            "- Must contain at least one lowercase letter\n" +
            "- Must contain at least one digit\n" +
            "- May contain special characters (e.g., !, @, #, $, %, etc.)")
    private final String password;
    @NotNull(message = "Email otp is required")
    private final String emailOtp;
    @NotNull(message = "Phone otp is required")
    private final String phoneOtp;
    @NotNull(message = "Customer id can not be empty")
    private final String customerId;
    @NotNull(message = "Customer id can not be empty")
    private final String credentialId;
}
