package com.spay.wallet.admin.controller;

import com.spay.wallet.admin.reqRes.AdminAccountResponse;
import com.spay.wallet.admin.reqRes.CreateAdminAccountRequest;
import com.spay.wallet.admin.service.AdminAccountService;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@PreAuthorize("hasAuthority('SUPER_ADMIN') || hasAuthority('ADMIN') ||  hasAuthority('DEV')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin-account")
public class AdminUserAccountController {
    private final AdminAccountService adminAccountService;

    @GetMapping("/details")
    public ResponseEntity<AdminAccountResponse> getAdminInfo(Principal principal){
        var id = UUID.fromString(principal.getName());
        return ResponseEntity.ok(adminAccountService.getAdminIfo(id));
    }


    @GetMapping("/user-accounts")
    public ResponseEntity<List<AdminAccountResponse>> getUserAccounts(){
        return ResponseEntity.ok(adminAccountService.getUserAccounts());
    }



    @PostMapping("/create-admin-account")
    public ResponseEntity<AdminAccountResponse> createNewAdminAccount(Principal principal, @RequestBody CreateAdminAccountRequest request){
        var id = UUID.fromString(principal.getName());
        var response = adminAccountService.createAdminAccount(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-admin-account")
    public ResponseEntity<Map<String,Object>> remove(Principal principal, @RequestParam String adminId){
        var id = UUID.fromString(principal.getName());
        var adId = UUID.fromString(adminId);
        if(adId.equals(id))
            throw new ApiException("Cannot delete your account", HttpStatus.NOT_ACCEPTABLE);
        adminAccountService.deleteAccount(adId);
        return ResponseEntity.ok(Map.of("message","Account is deleted successfully"));
    }

}
