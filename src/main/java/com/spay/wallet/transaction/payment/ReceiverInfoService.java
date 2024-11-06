package com.spay.wallet.transaction.payment;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.entities.AccountHolder;
import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.customer.services.CustomerService;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.properies.WalletProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiverInfoService {
    private final AccountBalanceService accountBalanceService;
    private final CustomerService customerService;
    private final WalletProperties walletProperties;


    public ReceiverInfoResponse getReceiverInfoSpay(String receiverAccount, PaymentChannel paymentChannel) {
        if(!paymentChannel.equals(PaymentChannel.INTERSPAY))
            throw new ApiException("Payment method not implemented",HttpStatus.NOT_ACCEPTABLE);
        receiverAccount = getReceiverAccount(receiverAccount);
        var account = accountBalanceService.getAccount(receiverAccount);
        if(List.of(AccountType.SETTLEMENT_ACCOUNT,AccountType.AGENT_ACCOUNT).contains(account.getAccountType()))
            throw new ApiException("Account not found",HttpStatus.NOT_FOUND);
        return getReceiverInfoResponse(receiverAccount, account);
    }

    private  String getReceiverAccount(String receiverAccount) {
        receiverAccount =  StringUtils.trimAllWhitespace(receiverAccount);
        if(!receiverAccount.contains(walletProperties.getAccountIdSuffix()))
            return receiverAccount+walletProperties.getAccountIdSuffix();
        return receiverAccount;
    }

    public ReceiverInfoResponse getReceiverInfoWithdrawal(String receiverAccount, PaymentChannel paymentChannel) {
        if(!paymentChannel.equals(PaymentChannel.INTERSPAY))
            throw new ApiException("Payment method not implemented",HttpStatus.NOT_ACCEPTABLE);
        receiverAccount = getReceiverAccount(receiverAccount);
        var account = accountBalanceService.getAccount(receiverAccount);
        if(!account.getAccountType().equals(AccountType.AGENT_ACCOUNT))
            throw new ApiException("Agent not found",HttpStatus.NOT_FOUND);
        return getReceiverInfoResponse(receiverAccount, account);
    }

    private ReceiverInfoResponse getReceiverInfoResponse(String receiverAccount, Account account) {
        var accountHolder = account.getAccountHolders().stream().filter(AccountHolder::getIsAdmin).findFirst().orElseThrow(() -> new ApiException("Account holder not found", HttpStatus.NOT_FOUND));
        var customer = customerService.getCustomerDetails(Long.parseLong(accountHolder.getAccountHolderId()));
        var customerFullName =  String.format("%s %s %s", customer.getFirstName(),customer.getMiddleName(),customer.getLastName());
        // TODO get exchange rate
        return new ReceiverInfoResponse(receiverAccount, account.getAccountName(), customerFullName, customer.getCustomerId(), customer.getProfileImage(), "", account.getCurrencyCode());
    }
}
