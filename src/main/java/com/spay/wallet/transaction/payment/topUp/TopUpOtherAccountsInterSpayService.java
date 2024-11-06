package com.spay.wallet.transaction.payment.topUp;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.admin.reqRes.AdminAccountResponse;
import com.spay.wallet.admin.service.AdminAccountService;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.payment.PaymentResponse;
import com.spay.wallet.transaction.payment.interspay.InterSpayPaymentService;
import com.spay.wallet.transaction.reqRes.TransactionRequest;
import com.spay.wallet.transaction.utilities.TransactionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopUpOtherAccountsInterSpayService {
    private final AccountBalanceService accountBalanceService;
    private final InterSpayPaymentService interSpayPaymentService;

    @Transactional
    public PaymentResponse topUpOtherAccount(TransactionRequest transactionRequest, Account senderAccount, String agentId) {
        accountBalanceService.checkIsAccountHolder(senderAccount,agentId);
        if(!senderAccount.getAccountType().equals(AccountType.AGENT_ACCOUNT))
            throw new ApiException("Agents only can top up accounts",HttpStatus.NOT_ACCEPTABLE);
        var payload = TransactionUtil.mapToPaymentPayload(transactionRequest);
        accountBalanceService.validateCommonPredicates(senderAccount,payload);
        var receiverAccount =  accountBalanceService.getAccount(payload.getReceiverAccount());
        isAllowedToTopUpTheirAccounts(receiverAccount.getAccountType());
        return interSpayPaymentService.transferCalculateAmounts(payload,senderAccount,receiverAccount);
    }



    private void isAllowedToTopUpTheirAccounts(AccountType accountType){
        var notAllowed = List.of(AccountType.SETTLEMENT_ACCOUNT,AccountType.AGENT_ACCOUNT);
        if(notAllowed.contains(accountType))
            throw new ApiException("Cannot top up this account",HttpStatus.NOT_ACCEPTABLE);
    }

}
