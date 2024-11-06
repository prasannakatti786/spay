package com.spay.wallet.transaction.payment.topUp;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.admin.reqRes.AdminAccountResponse;
import com.spay.wallet.admin.service.AdminAccountService;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.payment.PaymentPayload;
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
public class TopUpAgentAccountInterSpayService {
    private final AccountBalanceService accountBalanceService;
    private final AdminAccountService adminAccountService;
    private final InterSpayPaymentService interSpayPaymentService;
    @Transactional
    public PaymentResponse topUpAgentAccount(TransactionRequest transactionRequest, Account senderAccount, String adminId) {
        var payload = getPaymentPayload(transactionRequest, senderAccount, adminId);
        var receiverAccount =  accountBalanceService.getAccount(payload.getReceiverAccount());
        if(!receiverAccount.getAccountType().equals(AccountType.AGENT_ACCOUNT))
            throw new ApiException("Receiver account is not agent account", HttpStatus.NOT_ACCEPTABLE);
        return interSpayPaymentService.transferCalculateAmounts(payload,senderAccount,receiverAccount);
    }

    @Transactional
    public PaymentResponse topUpCustomerAccount(TransactionRequest transactionRequest, Account senderAccount, String adminId) {
        var payload = getPaymentPayload(transactionRequest, senderAccount, adminId);
        var receiverAccount =  accountBalanceService.getAccount(payload.getReceiverAccount());
        return interSpayPaymentService.transferCalculateAmounts(payload,senderAccount,receiverAccount);
    }

    private PaymentPayload getPaymentPayload(TransactionRequest transactionRequest, Account senderAccount, String adminId) {
        var adminAccount = adminAccountService.getAdminIfo(UUID.fromString(adminId));
        isAllowedToReplenishAgent(adminAccount);
        var description = String.format("%s(%s) had made settlement recharge amount of %s %.2f to this agent account %s", adminAccount.getFullName()
                , adminAccount.getUserType(), transactionRequest.getCurrencyCode(), transactionRequest.getAmount().doubleValue(), transactionRequest.getReceiverAccount());
        var payload = TransactionUtil.mapToPaymentPayload(transactionRequest,description);
        accountBalanceService.validateCommonPredicates(senderAccount,payload);
        return payload;
    }


    private void isAllowedToReplenishAgent(AdminAccountResponse accountResponse){
        var allowedUserTypes = List.of(UserType.ADMIN,UserType.SUPER_ADMIN);
        if(!allowedUserTypes.contains(accountResponse.getUserType()))
            throw new ApiException(String.format("User type is %s its not allowed to replenish agent account",accountResponse.getUserType()),HttpStatus.NOT_ACCEPTABLE);
    }

}
