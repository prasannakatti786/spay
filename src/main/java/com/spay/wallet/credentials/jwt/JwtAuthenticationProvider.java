package com.spay.wallet.credentials.jwt;

import com.spay.wallet.credentials.CredentialService;
import com.spay.wallet.credentials.WalletUserDetails;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final CredentialService userDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());
        WalletUserDetails userDetails = loadingUserDetails(username);
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            userDetailsService.successAttempts(userDetails.getCredentialId());
            return new UsernamePasswordAuthenticationToken(username, userDetails, userDetails.getAuthorities());
        }
        userDetailsService.countFailedAttempts(userDetails.getCredentialId(), userDetails.getCountFailedLoginAttempts());
        if(3-(userDetails.getCountFailedLoginAttempts()+1) == 0)
            throw new ApiException("Due to many failed attempts we locked your account, please connect support team", HttpStatus.FORBIDDEN);
        throw new ApiException("Username or password are incorrect, you have attempted "+(userDetails.getCountFailedLoginAttempts()+1)+" it will be locked after "+(3-(userDetails.getCountFailedLoginAttempts()+1)), HttpStatus.FORBIDDEN);
    }

    private WalletUserDetails loadingUserDetails(String username) {
        var userDetails = (WalletUserDetails) userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            log.info("Authentication have been failed to load user details");
            throw new BadCredentialsException("Internal server error");
        }
        if(!userDetails.isAccountNonExpired())
            throw new ApiException("Sorry user account is expired, please connect support team", HttpStatus.FORBIDDEN);
        if(userDetails.isAccountNonLocked())
            throw new ApiException("Due to many failed attempts we locked your account, please connect support team", HttpStatus.FORBIDDEN);
        if(!userDetails.isCredentialsNonExpired())
            throw new ApiException("You been using this password long time, please change your password go to forget password", HttpStatus.FORBIDDEN);
        if(!userDetails.isEnabled())
            throw new ApiException("Your account is disabled, please connect support team", HttpStatus.FORBIDDEN);

        return  userDetails;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
