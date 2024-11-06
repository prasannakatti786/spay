package com.spay.wallet.credentials;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotNull(message = "New Password can not be empty")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#@$!%*?&])[A-Za-z\\d#@$!%*?&]{8,}$",message = "Password Requirements:\n" +
                "- At least 8 characters long\n" +
                "- Less the 50 characters long\n" +
                "- Must contain at least one uppercase letter\n" +
                "- Must contain at least one lowercase letter\n" +
                "- Must contain at least one digit\n" +
                "- May contain special characters (e.g., !, @, #, $, %, etc.)")
        String newPassword,
        @NotNull(message = "Old Password can not be empty")
        String oldPassword) {
}
