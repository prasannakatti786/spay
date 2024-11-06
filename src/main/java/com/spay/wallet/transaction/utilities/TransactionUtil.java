package com.spay.wallet.transaction.utilities;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.common.WalletFormat;
import com.spay.wallet.properies.WalletProperties;
import com.spay.wallet.transaction.dto.ChargeResult;
import com.spay.wallet.transaction.dto.ExchangeResult;
import com.spay.wallet.transaction.dto.TransactionRecord;
import com.spay.wallet.transaction.entities.Transaction;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.payment.PaymentPayload;
import com.spay.wallet.transaction.reqRes.TransactionHistoryResponse;
import com.spay.wallet.transaction.reqRes.TransactionRequest;

import java.math.BigDecimal;

public class TransactionUtil {

    public static PaymentPayload mapToPaymentPayload(TransactionRequest request){
        return new PaymentPayload(request.getSenderAccount(), request.getReceiverAccount(),
                request.getCurrencyCode().name(), request.getAmount(), request.getPaymentChannel(),
                request.getAPin(), request.getLocation(), request.getDescription(), request.getTransactionType());
    }

    public static PaymentPayload mapToPaymentPayload(TransactionRequest request,String description){
        return new PaymentPayload(request.getSenderAccount(), request.getReceiverAccount(),
                request.getCurrencyCode().name(), request.getAmount(), request.getPaymentChannel(),
                request.getAPin(), request.getLocation(), description, request.getTransactionType());
    }


    public static TransactionHistoryResponse mapTransactionToTransactionHistoryResponse(Transaction transaction, String accountId, WalletProperties walletProperties){
        var info  = getInfo(transaction,accountId, walletProperties);
        return TransactionHistoryResponse.builder()
                .transactionId(transaction.getTransactionId())
                .xTransactionId(transaction.getXTransactionId() == null?"----": transaction.getXTransactionId())
                .debitAccount(transaction.getSenderAccount())
                .creditAccount(transaction.getReceiverAccount())
                .transactionDate(transaction.getTransactionDate())
                .transactionDateFormat(WalletFormat.formatDateTime(transaction.getTransactionDate()))
                .transactionType(transaction.getTransactionType())
                .paymentChannel(transaction.getPaymentChannel())
                .creditAccountName(transaction.getReceiverName())
                .debitAccountName(transaction.getSenderName())
                .status(transaction.getTransactionStatus())
                .description(transaction.getDescription())
                .amount(WalletFormat.currencyFormat(info.amount))
                .chargeFee(info.chargeRate == null? null: info.chargeRate.stripTrailingZeros().doubleValue()+"")
                .exchangeRate(transaction.getExchangeMessage() == null?"----":transaction.getExchangeMessage())
                .message(info.message)
                .isCredit(info.isCredit)
                .currencyType(info.currencyCode.getCurrencyType())
                .currentBalance(WalletFormat.currencyFormat(info.currentBalance))
                .currencyCode(info.currencyCode)
                .build();
    }


    public static String getSenderMessage(TransactionRecord record, Transaction transaction, String transactionId, PaymentChannel paymentChannel, WalletProperties walletProperties) {
        var message   = walletProperties.getLabelTemplate() + "\n"
                + walletProperties.getSenderTemplateMessage()
                .replace("sentAmount", record.getDebitCurrencyCode() + " " + WalletFormat.currencyFormat(record.getDebitAmount()))
                .replace("transactionDate", WalletFormat.formatDateTime(transaction.getTransactionDate()))
                .replace("refno", transactionId);
        message = message.replace("receiverNameReceiverAccount", (record.getReceiverName() == null? paymentChannel.name():record.getReceiverName()) + "(" + record.getReceiverAccount() + ")");
        message =  message.replace("senderAccount", record.getSenderAccount());
        return message;
    }

    public static String getReceiverMessage(TransactionRecord record, Transaction transaction, String transactionId,  PaymentChannel paymentChannel, WalletProperties walletProperties) {
        var message = walletProperties.getLabelTemplate() + "\n"
                + walletProperties.getReceiverTemplateMessage()
                .replace("sentAmount", record.getCreditCurrencyCode() + " " +  WalletFormat.currencyFormat(record.getCreditAmount()))
                .replace("transactionDate", WalletFormat.formatDateTime(transaction.getTransactionDate()))
                .replace("refno", transactionId);
        message = message.replace("senderNameSenderAccount", (record.getSenderName() == null? paymentChannel.name():record.getSenderName()) + "(" + record.getSenderAccount() + ")");
        message =  message.replace("receiverAccount", record.getReceiverAccount());
        return message;
    }


    private static Info getInfo(Transaction transaction,String myAccount, WalletProperties walletProperties){
        if(transaction.getSenderAccount().contains(myAccount))
            return new Info(transaction.getDebitAmount(),transaction.getDebitCurrentBalance(),transaction.getDebitCurrencyCode(),
                    getSenderMessage(new TransactionRecord(transaction),transaction,transaction.getTransactionId(),transaction.getPaymentChannel(), walletProperties),Boolean.FALSE,transaction.getCharge(),transaction.getExchangeFee());
        return new Info(transaction.getCreditAmount(),transaction.getCreditCurrentBalance(),transaction.getCreditCurrencyCode(),
                getReceiverMessage(new TransactionRecord(transaction),transaction,transaction.getTransactionId(),transaction.getPaymentChannel(), walletProperties),Boolean.TRUE,null,null);
    }
    private record Info(BigDecimal amount, BigDecimal currentBalance, CurrencyCode currencyCode, String message, Boolean isCredit, BigDecimal chargeRate, BigDecimal exchangeFee){}



    public static TransactionRecord createTransactionRecord(ChargeResult chargeResult, ExchangeResult exchangeResult, Account receiver, Account sender, PaymentPayload payload,TransactionStatus status){
        return  TransactionRecord.builder()
                .senderAccount(sender.getAccountId()).receiverAccount(receiver.getAccountId())
                .senderName(sender.getAccountName())
                .transactionType(payload.getTransactionType())
                .receiverName(receiver.getAccountName())
                .creditAmount(exchangeResult.getToExchangedAmount())
                .debitAmount(chargeResult.amountAndCharge()).charge(chargeResult.charge())
                .creditCurrencyCode(exchangeResult.getToCurrencyCode()).debitCurrencyCode(exchangeResult.getFromCurrencyCode())
                .creditCurrentBalance(receiver.getBalance()).debitCurrentBalance(sender.getBalance())
                .exchangeFee(exchangeResult.getExchangeRate())
                .exchangeMessage(exchangeResult.getMessage())
                .description(payload.getDescription())
                .transactionLocation(payload.getLocation()).transactionStatus(status)
                .paymentChannel(payload.getPaymentChannel()).build();

    }


}
