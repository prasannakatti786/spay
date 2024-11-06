package com.spay.wallet.transaction.payment.topUp;

import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.transaction.payment.PaymentResponse;
import com.spay.wallet.transaction.reqRes.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TopUpInterSpayService {
    private final AccountBalanceService accountBalanceService;
    private final TopUpAgentAccountInterSpayService topUpAgentAccountInterSpayService;
    private final TopUpOtherAccountsInterSpayService topUpOtherAccountsInterSpayService;

    public PaymentResponse topUpAccountInterSpay(TransactionRequest transactionRequest, String userId){
        var senderAccount = accountBalanceService.getAccount(transactionRequest.getSenderAccount());
        if(senderAccount.getAccountType().equals(AccountType.SETTLEMENT_ACCOUNT))
            return topUpAgentAccountInterSpayService.topUpAgentAccount(transactionRequest,senderAccount,userId);
        return topUpOtherAccountsInterSpayService.topUpOtherAccount(transactionRequest,senderAccount,userId);
    }



}
