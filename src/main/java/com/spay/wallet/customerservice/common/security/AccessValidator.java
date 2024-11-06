package com.spay.wallet.customerservice.common.security;
import com.spay.wallet.customerservice.common.exceptions.ApiException;
import com.spay.wallet.customerservice.common.security.ClientAppType;
import com.spay.wallet.customerservice.common.security.Principal;
import com.spay.wallet.customerservice.common.security.UserPermission;
import com.spay.wallet.customerservice.common.security.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AccessValidator {
    public static com.spay.wallet.customerservice.common.security.Principal validateOneRoleAndPermissions(HttpServletRequest request, @NonNull List<com.spay.wallet.customerservice.common.security.UserRole> userRole, @NonNull com.spay.wallet.customerservice.common.security.UserPermission... permissions){
        var principal = validateAndGetPrincipal(request);
        validateRoles(userRole, principal);
        List<com.spay.wallet.customerservice.common.security.UserPermission> allowedPermissions = Arrays.asList(permissions);
        if(allowedPermissions.isEmpty()) return principal;
        var userPermissions =principal.getPermissions();
        if(allowedPermissions.stream().noneMatch(userPermissions::contains))
            throw new ApiException("You don't have permission to access this service", HttpStatus.FORBIDDEN);
        return principal;
    }

    private static void validateRoles(List<com.spay.wallet.customerservice.common.security.UserRole> userRoles, com.spay.wallet.customerservice.common.security.Principal principal) {
        if(userRoles.contains(com.spay.wallet.customerservice.common.security.UserRole.ALL_ROLES)) return;
        if(!userRoles.contains(principal.getRole()))
            throw new ApiException("Your role is not allowed to access this service", HttpStatus.FORBIDDEN);
    }

    private static com.spay.wallet.customerservice.common.security.Principal validateAndGetPrincipal(HttpServletRequest request){
        validateHeader(request);
        var headerUserRole = UserRole.valueOf(Objects.requireNonNull(request.getHeader("x-auth-user-role")));
        var headerUserId = Objects.requireNonNull(request.getHeader("x-auth-user-id"));
        var clientAppType = ClientAppType.valueOf(Objects.requireNonNull(request.getHeader("x-auth-client-app-type")).toUpperCase());
        List<com.spay.wallet.customerservice.common.security.UserPermission> headerUserPermissions = new LinkedList<>();
        setUserPermissions(request, headerUserPermissions);
        var credentialId =  Objects.requireNonNull(request.getHeader("x-auth-credential-id"));
        var deviceId = Objects.requireNonNull(request.getHeader("x-auth-device-id"));
        return new Principal(headerUserId,credentialId,deviceId,headerUserPermissions,headerUserRole,clientAppType);
    }

    private static void setUserPermissions(HttpServletRequest request, List<com.spay.wallet.customerservice.common.security.UserPermission> headerUserPermissions) {
        if(StringUtils.isEmpty(request.getHeader("x-auth-user-permissions"))) return;
        request.getHeaders("x-auth-user-permissions").asIterator().forEachRemaining(permission-> {
            try {
                var userPermission= UserPermission.valueOf(permission);
                headerUserPermissions.add(userPermission);
            }catch (RuntimeException ignored){
            }
        });
    }


    private static void validateHeader(HttpServletRequest request) {
        if(StringUtils.isEmpty(request.getHeader("x-auth-user-id")))
            throw new ApiException("Missing middle layer user id header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader("x-auth-user-role")))
            throw new ApiException("Missing middle layer user role header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader("x-auth-credential-id")))
            throw new ApiException("Missing middle layer credential id header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader("x-auth-device-id")))
            throw new ApiException("Missing middle layer device id header", HttpStatus.FORBIDDEN);
        if(StringUtils.isEmpty(request.getHeader( "x-auth-client-app-type")))
            throw new ApiException("Missing client app type header", HttpStatus.FORBIDDEN);
    }
}

