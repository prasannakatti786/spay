package com.spay.wallet.customerservice.common.security;


import com.spay.wallet.customerservice.common.exceptions.ApiException;
import com.spay.wallet.customerservice.common.security.UserRole;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public enum UserPermission {
    CREATE(700,"Create", UserRole.ALL_ROLES),
    UPDATE(800,"Update",UserRole.ALL_ROLES),
    DELETE(600,"Delete", UserRole.ALL_ROLES),
    READ(900,"Read", UserRole.ALL_ROLES),
    ADD_NOTIFICATION_CHANNEL(701,"Add notification channel", UserRole.ALL_ROLES),
    CUSTOMER_DETAIL(901,"Get customer details", UserRole.CUSTOMER,UserRole.ADMIN, UserRole.SUPER_ADMIN),
    CREATE_WALLET_ACCOUNT(742,"Create wallet account",UserRole.CUSTOMER,UserRole.SUPER_ADMIN,UserRole.ADMIN);

    private final Integer permissionCode;
    private final String readablePermission;
    private final UserRole[] userRoles;

    UserPermission(Integer permissionCode, String readablePermission, UserRole ... userRoles) {
        this.permissionCode = permissionCode;
        this.readablePermission = readablePermission;
        this.userRoles = userRoles;
    }

    public static List<com.spay.wallet.customerservice.common.security.UserPermission> getUserPermissionsByUserRole(UserRole userRole){
        return Arrays.stream(com.spay.wallet.customerservice.common.security.UserPermission.values())
                .filter(userPermission -> Arrays.asList(userPermission.getUserRoles()).contains(userRole))
                .toList();
    }
    public static List<com.spay.wallet.customerservice.common.security.UserPermission> getAllPermissions(){
        return Arrays.asList(com.spay.wallet.customerservice.common.security.UserPermission.values());
    }

    public static com.spay.wallet.customerservice.common.security.UserPermission getPermissionByCode(Integer permissionCode){
        return Arrays.stream(com.spay.wallet.customerservice.common.security.UserPermission.values())
                .filter(permission-> Objects.equals(permission.getPermissionCode(), permissionCode))
                .findFirst().orElseThrow(()->new ApiException("Invalid permission", HttpStatus.BAD_REQUEST));
    }
}