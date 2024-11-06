package com.spay.wallet.admin.service;

import com.spay.wallet.admin.enties.AdminAccount;
import com.spay.wallet.admin.repo.AdminAccountRepository;
import com.spay.wallet.admin.reqRes.AdminAccountResponse;
import com.spay.wallet.admin.reqRes.CreateAdminAccountRequest;
import com.spay.wallet.credentials.Credential;
import com.spay.wallet.credentials.CredentialRepository;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAccountService {
    private final AdminAccountRepository adminAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialRepository credentialRepository;

    public AdminAccountResponse getAdminIfo(UUID id){
        return adminAccountRepository.findById(id)
                .map(adminAccount -> new AdminAccountResponse(adminAccount.getId(),adminAccount.getFullName(), adminAccount.getEmail(),
                        adminAccount.getCredential().getUserType()))
                .orElseThrow(() -> new ApiException("Admin account is not exist", HttpStatus.FORBIDDEN));
    }

    public AdminAccountResponse createAdminAccount(CreateAdminAccountRequest request){
        if(!List.of(UserType.ADMIN,UserType.SUPER_ADMIN).contains(request.getUserType()))
            throw new ApiException("Usertype must be admin or super admin", HttpStatus.NOT_ACCEPTABLE);
        var isExistEmail = credentialRepository.existsByUsernameEmail(request.getEmail().toLowerCase());
        if(isExistEmail)
            throw new ApiException("This email is already exist",HttpStatus.BAD_REQUEST);
        var isExistPhone = credentialRepository.existsByUsernamePhone(request.getPhoneNumber());
        if(isExistPhone)
            throw new ApiException("This phone number is already exist",HttpStatus.BAD_REQUEST);

        var admin = new AdminAccount();
        admin.setId(UUID.randomUUID());
        admin.setEmail(request.getEmail());
        admin.setFullName(request.getFullName());
        Credential credential =  Credential.builder()
                .accountExpiresAt(LocalDateTime.now().plusYears(13))
                .credentialExpireAt(LocalDateTime.now().plusYears(13))
                .countFailedLoginAttempts(0)
                .isLocked(Boolean.FALSE)
                .isEnabled(Boolean.TRUE)
                .userId(admin.getId().toString())
                .usernameEmail(request.getEmail())
                .usernamePhone(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(request.getUserType())
                .build();
           admin.setCredential(credential);
        adminAccountRepository.saveAndFlush(admin);
        return new AdminAccountResponse(admin.getId(),admin.getFullName(), admin.getEmail(),
                admin.getCredential().getUserType());
    }

    public void createDevAccount(String name,String password, String email, String phoneNumber){
        var isExist = adminAccountRepository.existsByCredential_UserType(UserType.DEV);
        if(isExist){
            log.info("Dev account is already exist");
            return;
        }

        var admin = new AdminAccount();
        admin.setId(UUID.randomUUID());
        admin.setEmail(email);
        admin.setFullName(name);
        Credential credential =  Credential.builder()
                .accountExpiresAt(LocalDateTime.now().plusYears(13))
                .countFailedLoginAttempts(0)
                .credentialExpireAt(LocalDateTime.now().plusYears(13))
                .isLocked(Boolean.FALSE)
                .isEnabled(Boolean.TRUE)
                .userId(admin.getId().toString())
                .usernameEmail(email)
                .password(passwordEncoder.encode(password))
                .usernamePhone(phoneNumber)
                .userType(UserType.DEV)
                .build();
        admin.setCredential(credential);
        adminAccountRepository.save(admin);
        log.info("Dev account is created successfully");
    }

    public void deleteAccount(UUID adId) {
        adminAccountRepository.deleteById(adId);
    }

    public List<AdminAccountResponse> getUserAccounts() {
        return adminAccountRepository.findAll().stream().map( adminAccount -> new AdminAccountResponse(adminAccount.getId(),adminAccount.getFullName(), adminAccount.getEmail(),
                adminAccount.getCredential().getUserType())).toList();
    }
}
