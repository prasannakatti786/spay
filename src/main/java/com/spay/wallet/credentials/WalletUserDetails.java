package com.spay.wallet.credentials;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class WalletUserDetails implements UserDetails {
    @Getter
    private final Long credentialId;
    @Getter
    private final String userId;
    @Getter
    private final UserType userType;

    private final String username;
    private final String password;
    private final Boolean isEnabled;
    @Getter
    private final Boolean isEnabledTwoFactorAuth;
    @Getter
    private final Credential credential;
    private final Boolean isLocked;
    private final LocalDateTime accountExpiresAt;
    private final LocalDateTime credentialExpireAt;
    @Getter
    private final Integer countFailedLoginAttempts;
    private final List<SimpleGrantedAuthority> authorities;


    public WalletUserDetails(Credential credential, String username){
        this.credentialId =  credential.getId();
        this.userId =  credential.getUserId();
        this.userType =  credential.getUserType();
        this.username = username;
        this.password =  credential.getPassword();
        this.isEnabled =  credential.getIsEnabled();
        this.accountExpiresAt =  credential.getAccountExpiresAt();
        this.credentialExpireAt = credential.getCredentialExpireAt();
        this.countFailedLoginAttempts =  credential.getCountFailedLoginAttempts();
        this.isLocked =  credential.getIsLocked();
        this.authorities = List.of(new SimpleGrantedAuthority(credential.getUserType().name()));
        isEnabledTwoFactorAuth = credential.getEnabledTwoFactorAuth() == null? Boolean.FALSE:credential.getEnabledTwoFactorAuth();
        this.credential = credential;
    }




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isLocked;

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
