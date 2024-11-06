package com.spay.wallet.customerservice.common.security;

import com.spay.wallet.customerservice.common.security.ClientAppType;
import com.spay.wallet.customerservice.common.security.UserPermission;
import com.spay.wallet.customerservice.common.security.UserRole;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Principal {
    private  String userId;
    private String credentialId;
    private String deviceId;
    private List<UserPermission> permissions;
    private UserRole role;
    private ClientAppType clientAppType;
}
