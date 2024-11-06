package com.spay.wallet.transaction.payment.withdrawal;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.properies.WalletProperties;
import com.spay.wallet.transaction.entities.Transaction;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.payment.PaymentResponse;
import com.spay.wallet.transaction.reqRes.TransactionRequest;
import com.spay.wallet.transaction.service.ChargeAndExchangeService;
import com.spay.wallet.transaction.service.TransactionRecordService;
import com.spay.wallet.transaction.utilities.TransactionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AgentWithdrawalService {
    private final AccountBalanceService accountBalanceService;
    private final ChargeAndExchangeService chargeAndExchangeService;
    private final TransactionRecordService transactionRecordService;
    private final WalletProperties walletProperties;


    public PaymentResponse withdrawMoneyByAgent(TransactionRequest transactionRequest, String customerId) {
        var senderAccount = accountBalanceService.getAccount(transactionRequest.getSenderAccount());
        accountBalanceService.checkIsAccountHolder(senderAccount,customerId);
        accountBalanceService.isAllowedToWithdrawOrTransferMoney(senderAccount);
        var payload = TransactionUtil.mapToPaymentPayload(transactionRequest);
        accountBalanceService.validateCommonPredicates(senderAccount, payload);
        var receiverAccount =  accountBalanceService.getAccount(payload.getReceiverAccount());
        if(!receiverAccount.getAccountType().equals(AccountType.AGENT_ACCOUNT))
            throw new ApiException("Receiver account is not agent", HttpStatus.NOT_ACCEPTABLE);
        var charge = chargeAndExchangeService.calculateCharge(payload.getAmount(), payload.getPaymentChannel(),transactionRequest.getTransactionType());
        accountBalanceService.isAccountAllowedForSending(senderAccount);
        accountBalanceService.isAccountAllowedForReceiving(receiverAccount);
        accountBalanceService.isEnoughBalance(senderAccount, charge.amountAndCharge());
        accountBalanceService.changeBalanceIntoPending(senderAccount, charge.amountAndCharge());
        var exchange =  chargeAndExchangeService.calculateExchange(charge.amount(), senderAccount.getCurrencyCode(), receiverAccount.getCurrencyCode());
        var transactionRecord = TransactionUtil.createTransactionRecord(charge, exchange, receiverAccount, senderAccount, payload, TransactionStatus.PENDING);
        var transaction = transactionRecordService.saveTransactionRecord(transactionRecord);
        return new PaymentResponse(transaction.getTransactionId(), payload.getCurrencyCode(), payload.getAmount(), transaction);
    }

    public PaymentResponse acceptWithdrawalAgent(Long transactionId,String agentAccountId,String aPin, String agentUserId){
        var agentAccount = getAgentAccount(agentAccountId);
        accountBalanceService.checkAPIN(agentAccount,aPin);
        var transaction = getPendingTransaction(transactionId, agentAccountId, agentUserId, agentAccount);
        try {
            var customerAccount = accountBalanceService.getAccount(transaction.getSenderAccount());
            accountBalanceService.isAllowedToWithdrawOrTransferMoney(customerAccount);
            accountBalanceService.addPendingBalance(agentAccount,transaction.getCreditAmount());
            accountBalanceService.changePendingIntoBalance(agentAccount,transaction.getCreditAmount());
            accountBalanceService.removePendingBalance(customerAccount,transaction.getDebitAmount());
            transactionRecordService.updateTransactionStatus(transactionId,TransactionStatus.COMPLETED);
            var transactionHistoryResponse =  TransactionUtil.mapTransactionToTransactionHistoryResponse(transaction,agentAccountId,walletProperties);
            return new PaymentResponse(transaction.getTransactionId(), transaction.getCreditCurrencyCode().name(), transaction.getCreditAmount(), transactionHistoryResponse);
        }catch (ApiException e){
            transactionRecordService.updateTransactionStatus(transactionId,TransactionStatus.FAILED);
            throw e;
        }
    }

    public PaymentResponse declineWithdrawalAgent(Long transactionId,String agentAccountId,String aPin, String agentUserId){
        var agentAccount = getAgentAccount(agentAccountId);
        accountBalanceService.checkAPIN(agentAccount,aPin);
        var transaction = getPendingTransaction(transactionId, agentAccountId, agentUserId, agentAccount);
        try {
            var refundAmount = transaction.getDebitAmount();
            var customerAccount = accountBalanceService.getAccount(transaction.getSenderAccount());
            accountBalanceService.changePendingIntoBalance(customerAccount,refundAmount);
            transactionRecordService.updateTransactionStatus(transactionId,TransactionStatus.FAILED);
            var transactionHistoryResponse =  TransactionUtil.mapTransactionToTransactionHistoryResponse(transaction,agentAccountId,walletProperties);
            return new PaymentResponse(transaction.getTransactionId(), transaction.getCreditCurrencyCode().name(), transaction.getCreditAmount(), transactionHistoryResponse);
        }catch (ApiException e){
            transactionRecordService.updateTransactionStatus(transactionId,TransactionStatus.FAILED);
            throw e;
        }
    }

    private Transaction getPendingTransaction(Long transactionId, String agentAccountId, String agentUserId, Account agentAccount) {
        accountBalanceService.checkIsAccountHolder(agentAccount, agentUserId);
        accountBalanceService.isAccountAllowedForReceiving(agentAccount);
        var transaction =  transactionRecordService.getTransaction(transactionId);
        if(!transaction.getTransactionStatus().equals(TransactionStatus.PENDING))
            throw new ApiException("This transaction is not available",HttpStatus.NOT_EXTENDED);
        if (!transaction.getReceiverAccount().equals(agentAccountId))
            throw new ApiException("This transaction is not available", HttpStatus.NOT_EXTENDED);
        return transaction;
    }

    private Account getAgentAccount(String agentAccountId) {
        var agentAccount= accountBalanceService.getAccount(agentAccountId);
        if(!agentAccount.getAccountType().equals(AccountType.AGENT_ACCOUNT))
            throw new ApiException("This account is not agent", HttpStatus.NOT_ACCEPTABLE);
        return agentAccount;
    }


}
