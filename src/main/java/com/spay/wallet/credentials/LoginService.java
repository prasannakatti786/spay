package com.spay.wallet.credentials;

import com.spay.wallet.credentials.jwt.JwtUtility;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.services.CustomerRegistrationService;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.messages.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final AuthenticationProvider authenticationProvider;
    private final JwtUtility jwtUtility;
    private final CredentialRepository credentialRepository;
    private final CustomerRegistrationService customerRegistrationService;


    public TokenResponse login(LoginRequest request) {
        var userDetails = validateUserCredentials(request);
        if(!userDetails.getUserType().equals(request.getUserType()))
            throw new ApiException("User type is not matching",HttpStatus.FORBIDDEN);
        if(userDetails.getIsEnabledTwoFactorAuth())
            return generateOtpAndSend(userDetails);
        return getTokenResponse(userDetails);
    }

    private WalletUserDetails validateUserCredentials(LoginRequest request) {
        var username =  request.getUsername();
        var password =  request.getPassword();
        if(request.getUserType() ==  null)
            throw new ApiException("User type is required",HttpStatus.BAD_REQUEST);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticated = authenticationProvider.authenticate(token);
        return (WalletUserDetails) authenticated.getCredentials();
    }

    private TokenResponse generateOtpAndSend(WalletUserDetails walletUserDetails){
        var credential =walletUserDetails.getCredential();
        if(credential.getMessageChannel() == null || MessageChannel.ALL.equals(credential.getMessageChannel())){
            customerRegistrationService.generateOtpAndSave(credential);
        }
        else if(List.of(MessageChannel.SMS,MessageChannel.WHATSAPP).contains(credential.getMessageChannel())){
            customerRegistrationService.generateSaveAndSendPhoneOtp(credential);
        }
        else if(MessageChannel.EMAIL.equals(credential.getMessageChannel())){
            customerRegistrationService.generateSaveAndSendEmailOtp(credential);
        }
        else
            return getTokenResponse(walletUserDetails);
        return new TokenResponse(null,null,Boolean.TRUE,"Otp is sent",
                credential.getMessageChannel()==null?MessageChannel.ALL:credential.getMessageChannel());
    }

    public TokenResponse verifyTwoFactorAuth(LoginRequest request){
        var userDetails = validateUserCredentials(request);
        if(!userDetails.getUserType().equals(request.getUserType()))
            throw new ApiException("User type is not matching",HttpStatus.FORBIDDEN);
        var credential = userDetails.getCredential();
        if(credential.getMessageChannel() == null || MessageChannel.ALL.equals(credential.getMessageChannel())) {
            customerRegistrationService.validateEmailOtp(userDetails.getCredential(), request.getEmailOtp());
            //customerRegistrationService.validatePhoneOtp(userDetails.getCredential(), request.getPhoneOtp());
            credential.setOtpPhone(null);
            credential.setOtpEmail(null);
            credential.setOtpExpireDateEmail(LocalDateTime.now());
            credential.setOtpExpireDatePhone(LocalDateTime.now());
            credentialRepository.save(credential);
            return getTokenResponse(userDetails);
        }
        else if(List.of(MessageChannel.SMS,MessageChannel.WHATSAPP).contains(credential.getMessageChannel())){
            customerRegistrationService.validatePhoneOtp(userDetails.getCredential(), request.getPhoneOtp());
            credential.setOtpPhone(null);
            credential.setOtpExpireDatePhone(LocalDateTime.now());
            credentialRepository.save(credential);
            return getTokenResponse(userDetails);
        }
        else if(MessageChannel.EMAIL.equals(credential.getMessageChannel())){
            customerRegistrationService.validateEmailOtp(userDetails.getCredential(), request.getEmailOtp());
            credential.setOtpEmail(null);
            credential.setOtpExpireDateEmail(LocalDateTime.now());
            credentialRepository.save(credential);
            return getTokenResponse(userDetails);
        }
        return getTokenResponse(userDetails);
    }

    private TokenResponse getTokenResponse(WalletUserDetails userDetails) {
        var sessionId = CredentialUtil.createOTP(20);
        var accessToken = jwtUtility.generateJWT(userDetails.getUserType(), sessionId, userDetails.getCredentialId(), userDetails.getUserId());
        credentialRepository.updateSessionIdById(sessionId, userDetails.getCredentialId());
        return accessToken;
    }

    public void logout(String customerId) {
        var credential = credentialRepository.findByUserId(customerId).orElseThrow(() -> new ApiException("Customer does not exist", HttpStatus.NOT_FOUND));
        credentialRepository.updateSessionIdById("",credential.getId());
    }
}
