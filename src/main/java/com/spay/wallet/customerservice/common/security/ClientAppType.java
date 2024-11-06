package com.spay.wallet.customerservice.common.security;


import com.spay.wallet.customerservice.common.security.UserRole;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ClientAppType {
    AGENT_APP(com.spay.wallet.customerservice.common.security.UserRole.AGENT),
    AGENT_DESKTOP(com.spay.wallet.customerservice.common.security.UserRole.AGENT),
    SUPER_ADMIN_WEB(com.spay.wallet.customerservice.common.security.UserRole.SUPER_ADMIN, com.spay.wallet.customerservice.common.security.UserRole.DEV),
    ADMIN_WEB(com.spay.wallet.customerservice.common.security.UserRole.SUPER_ADMIN, com.spay.wallet.customerservice.common.security.UserRole.DEV),
    CUSTOMER_WEB(com.spay.wallet.customerservice.common.security.UserRole.CUSTOMER),
    CUSTOMER_APP(com.spay.wallet.customerservice.common.security.UserRole.CUSTOMER),
    CUSTOMER_DESKTOP(com.spay.wallet.customerservice.common.security.UserRole.CUSTOMER);
    private final com.spay.wallet.customerservice.common.security.UserRole[] roles;
    ClientAppType(com.spay.wallet.customerservice.common.security.UserRole... roles) {
        this.roles = roles;
    }

    public static boolean isWeb(com.spay.wallet.customerservice.common.security.ClientAppType clientAppType) {
        var webClients = List.of(CUSTOMER_WEB,SUPER_ADMIN_WEB,ADMIN_WEB);
        return webClients.contains(clientAppType);
    }

    public Boolean isRoleAllowedClientAppType(UserRole userRole){
        return Arrays.asList(roles).contains(userRole);
    }
}
