package com.spay.wallet.admin.reqRes;

import com.spay.wallet.credentials.UserType;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateAdminAccountRequest {
    private final String fullName;
    private final String phoneNumber;
    private final String email;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#@$!%*?&])[A-Za-z\\d#@$!%*?&]{8,}$",message = "Password Requirements:\n" +
            "- At least 8 characters long\n" +
            "- Less the 50 characters long\n" +
            "- Must contain at least one uppercase letter\n" +
            "- Must contain at least one lowercase letter\n" +
            "- Must contain at least one digit\n" +
            "- May contain special characters (e.g., !, @, #, $, %, etc.)")
    private final String password;
    private final UserType userType;
}
