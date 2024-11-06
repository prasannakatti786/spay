package com.spay.wallet.credentials;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/credential")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @PutMapping("/change-password")
    public ResponseEntity<TokenResponse> login(@RequestBody ChangePasswordRequest request, @RequestAttribute String credentialId) {
        var token = credentialService.changePassword(request, Long.parseLong(credentialId));
        System.out.println(credentialId);
        return ResponseEntity.ok(token);
    }

}
