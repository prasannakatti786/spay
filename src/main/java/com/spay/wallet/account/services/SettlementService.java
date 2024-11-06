package com.spay.wallet.account.services;

import com.spay.wallet.account.reqAndRes.accountCreation.AccountDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final AccountService accountService;
    private final AccountCreationService accountCreationService;

    public AccountDetailsResponse getAccounts(){
        return accountService.getSettlementAccount();
    }

    public void createSettlementAccount(){
        accountCreationService.createSettlementAccount();
    }


}
