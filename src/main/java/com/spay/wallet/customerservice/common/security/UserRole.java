package com.spay.wallet.customerservice.common.security;
public enum UserRole {
    ADMIN, SUPER_ADMIN, AGENT, CUSTOMER,DEV,ALL_ROLES;

    public static boolean isAdmin(com.spay.wallet.customerservice.common.security.UserRole userRole) {
        return ADMIN.equals(userRole) || SUPER_ADMIN.equals(userRole);
    }
}