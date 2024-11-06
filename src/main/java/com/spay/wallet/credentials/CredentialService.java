package com.spay.wallet.credentials;

import com.spay.wallet.credentials.jwt.JwtUtility;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CredentialService implements UserDetailsService {
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var credential = credentialRepository.findByUsernameEmailOrUsernamePhone(username);
        if(credential.isEmpty())
            throw new ApiException("Username or password is incorrect", HttpStatus.FORBIDDEN);
        return new WalletUserDetails(credential.get(), username);
    }

    public void countFailedAttempts(Long credentialId, Integer previousCounts){
        var credential =  credentialRepository.findById(credentialId).orElseThrow(() ->  new ApiException("Username or password is incorrect", HttpStatus.FORBIDDEN));
        credential.setCountFailedLoginAttempts(previousCounts+1);
        if(credential.getCountFailedLoginAttempts() == 3)
            credential.setIsLocked(Boolean.TRUE);
        credentialRepository.save(credential);
    }

    public void successAttempts(Long credentialId){
        var credential =  credentialRepository.findById(credentialId).orElseThrow(() ->  new ApiException("Username or password is incorrect", HttpStatus.FORBIDDEN));
        credential.setCountFailedLoginAttempts(0);
        credentialRepository.save(credential);
    }

    public TokenResponse changePassword(ChangePasswordRequest request, Long id){
        var credential =  credentialRepository.findById(id).orElseThrow(() ->  new ApiException("User credential not found", HttpStatus.FORBIDDEN));
        if(request.oldPassword().equals(request.newPassword()))
            throw new ApiException("New password is same to your old password",HttpStatus.NOT_ACCEPTABLE);
        if(!passwordEncoder.matches(request.oldPassword(), credential.getPassword()))
            throw new ApiException("Old Password is not correct",HttpStatus.NOT_ACCEPTABLE);
        credential.setPassword(passwordEncoder.encode(request.newPassword()));
        var sessionId = CredentialUtil.createOTP(20);
        credential.setSessionId(sessionId);
        var tokenResponse = jwtUtility.generateJWT(credential.getUserType(),sessionId,id,credential.getUserId());
        credentialRepository.save(credential);
        return tokenResponse;
    }



}
