package com.spay.wallet.transaction.payment.interspay;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.transaction.payment.PaymentPayload;
import com.spay.wallet.transaction.payment.PaymentResponse;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.service.ChargeAndExchangeService;
import com.spay.wallet.transaction.service.TransactionRecordService;
import com.spay.wallet.transaction.utilities.TransactionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InterSpayPaymentService  {
    private final AccountBalanceService accountBalanceService;
    private final TransactionRecordService transactionRecordService;
    private final ChargeAndExchangeService  chargeAndExchangeService;
    @Transactional
    public PaymentResponse transferMoney(PaymentPayload payload,String customerId) {
        var senderAccount = accountBalanceService.getAccount(payload.getSenderAccount());
        accountBalanceService.isAllowedToWithdrawOrTransferMoney(senderAccount);
        accountBalanceService.checkAPIN(senderAccount, payload.getAPin());
        accountBalanceService.checkIsSameAccount(payload.getSenderAccount(), payload.getReceiverAccount());
        accountBalanceService.isZeroAmount(payload.getAmount());
        accountBalanceService.isMoreThen2Dec(payload.getCurrencyCode(),payload.getAmount());
        accountBalanceService.checkIsAccountHolder(senderAccount,customerId);
        accountBalanceService.checkDuplicatePaymentRequest(senderAccount,payload);
        var receiverAccount =  accountBalanceService.getAccount(payload.getReceiverAccount());
        return transferCalculateAmounts(payload, senderAccount, receiverAccount);
    }
    @Transactional
    public PaymentResponse transferCalculateAmounts(PaymentPayload payload, Account senderAccount, Account receiverAccount) {
        var charge = chargeAndExchangeService.calculateCharge(payload.getAmount(), payload.getPaymentChannel(),payload.getTransactionType());
        validatePayment(senderAccount, receiverAccount, charge.amountAndCharge());
        accountBalanceService.changeBalanceIntoPending(senderAccount, charge.amountAndCharge());
        var exchange =  chargeAndExchangeService.calculateExchange(charge.amount(), senderAccount.getCurrencyCode(), receiverAccount.getCurrencyCode());
        accountBalanceService.addPendingBalance(receiverAccount,exchange.getToExchangedAmount());
        accountBalanceService.changePendingIntoBalance(receiverAccount,exchange.getToExchangedAmount());
        accountBalanceService.removePendingBalance(senderAccount,charge.amountAndCharge());
        var transactionRecord = TransactionUtil.createTransactionRecord(charge, exchange, receiverAccount, senderAccount, payload,TransactionStatus.COMPLETED);
        var transaction = transactionRecordService.saveTransactionRecord(transactionRecord);
        return new PaymentResponse(transaction.getTransactionId(), payload.getCurrencyCode(), payload.getAmount(), transaction);
    }


    private void validatePayment(Account senderAccount, Account receiverAccount, BigDecimal amount) {
        accountBalanceService.isAccountAllowedForSending(senderAccount);
        accountBalanceService.isAccountAllowedForReceiving(receiverAccount);
        accountBalanceService.isEnoughBalance(senderAccount, amount);
    }




}
