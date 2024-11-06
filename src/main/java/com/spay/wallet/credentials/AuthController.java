package com.spay.wallet.credentials;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request){
       TokenResponse tokenResponse = loginService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/two-factor-auth")
    public ResponseEntity<TokenResponse> twoFactorAuth(@RequestBody LoginRequest request){
        TokenResponse tokenResponse = loginService.verifyTwoFactorAuth(request);
        return ResponseEntity.ok(tokenResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>> logout(Principal principal){
        var userId =  principal.getName();
        loginService.logout(userId);
        return ResponseEntity.ok(Map.of("message","Logout successfully"));
    }
}
