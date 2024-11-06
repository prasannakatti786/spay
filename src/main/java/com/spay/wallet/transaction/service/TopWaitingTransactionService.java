package com.spay.wallet.transaction.service;

import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.account.services.SettlementService;
import com.spay.wallet.credentials.CredentialUtil;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.entities.TopUpWaitingTransaction;
import com.spay.wallet.transaction.entities.TransactionLocation;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.payment.topUp.TopUpAgentAccountInterSpayService;
import com.spay.wallet.transaction.payment.topUp.TopUpInterSpayService;
import com.spay.wallet.transaction.repo.TopUpWaitingTransactionRepository;
import com.spay.wallet.transaction.reqRes.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TopWaitingTransactionService {
    private final TopUpWaitingTransactionRepository topUpWaitingTransactionRepository;
    private final SettlementService settlementService;
    private final AccountBalanceService accountBalanceService;
    private final TopUpAgentAccountInterSpayService topUpAgentAccountInterSpayService;

    public String createTopUpWaitingTransaction(TopUpWaitingTransaction transaction) {
        var secret = CredentialUtil.createOTP(8);
        transaction.setSecretNo(secret);
        topUpWaitingTransactionRepository.save(transaction);
        return secret;
    }

    public void topUpAccount(Long id, String adminId, String aPin,Double amount) {
        var transaction = topUpWaitingTransactionRepository.findById(id).orElseThrow(() -> new ApiException("Not Found", HttpStatus.BAD_REQUEST));
        var account = settlementService.getAccounts();
        var location = new TransactionLocation("AN", "Server", "23144213", "Linux", "0.00", "0.00");
        var transactionRequest = new TransactionRequest(account.getAccountNumber(), transaction.getReceiverAccount(), transaction.getCurrencyCode(), BigDecimal.valueOf(amount), PaymentChannel.INTERSPAY, TransactionType.TOP_UP, aPin, location, "");
        var senderAccount = accountBalanceService.getAccount(transactionRequest.getSenderAccount());
        topUpAgentAccountInterSpayService.topUpCustomerAccount(transactionRequest,senderAccount,adminId);
        topUpWaitingTransactionRepository.deleteById(transaction.getId());
    }

    public List<TopUpWaitingTransaction> getTopUps(String accountId){
        return topUpWaitingTransactionRepository.findByReceiverAccount(accountId);
    }
}
