package com.spay.wallet.accountservice.controller;


import com.spay.wallet.accountservice.controller.services.AccountCreationService;
import com.spay.wallet.accountservice.controller.services.NewAccountReqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountCreationController {
    private final AccountCreationService accountCreationService;
    private final NewAccountReqService newAccountReqService;

    @PostMapping("/wallet/by-customer")
    @UserAuthorize(roles = UserRole.CUSTOMER,permissions = UserPermission.CREATE_WALLET_ACCOUNT)
    public ResponseEntity<CommonResponse> createNewWalletAccountByCustomer(Principal principal, @RequestBody @Valid CreateAccountRequest request){
        var response =  newAccountReqService.createNewAccount(principal.getUserId(), AccountType.WALLET_ACCOUNT,request);
        return ResponseEntity.ok(response);
    }
}
