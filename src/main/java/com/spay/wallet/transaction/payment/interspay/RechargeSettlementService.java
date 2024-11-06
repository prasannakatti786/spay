package com.spay.wallet.transaction.payment.interspay;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.admin.reqRes.AdminAccountResponse;
import com.spay.wallet.admin.service.AdminAccountService;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.dto.ChargeResult;
import com.spay.wallet.transaction.dto.ExchangeResult;
import com.spay.wallet.transaction.dto.TransactionRecord;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.payment.PaymentResponse;
import com.spay.wallet.transaction.reqRes.TransactionRequest;
import com.spay.wallet.transaction.service.TransactionRecordService;
import com.spay.wallet.transaction.utilities.TransactionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RechargeSettlementService {
    private final AccountBalanceService accountBalanceService;
    private final TransactionRecordService transactionRecordService;
    private final AdminAccountService adminAccountService;
    @Transactional
    public PaymentResponse rechargeSettlement(TransactionRequest transactionRequest, String customerId) {
        var adminAccount = adminAccountService.getAdminIfo(UUID.fromString(customerId));
        isAllowedToRechargeSettlement(adminAccount);
        var account = accountBalanceService.getAccount(transactionRequest.getReceiverAccount());
        if(!account.getAccountType().equals(AccountType.SETTLEMENT_ACCOUNT))
            throw new ApiException("This account is not settlement account", HttpStatus.NOT_ACCEPTABLE);
        accountBalanceService.checkAPIN(account, transactionRequest.getAPin());
        accountBalanceService.isZeroAmount(transactionRequest.getAmount());
        accountBalanceService.isMoreThen2Dec(transactionRequest.getCurrencyCode().name(),transactionRequest.getAmount());
        accountBalanceService.checkDuplicatePaymentRequest(account, TransactionUtil.mapToPaymentPayload(transactionRequest));
        accountBalanceService.addPendingBalance(account,transactionRequest.getAmount());
        accountBalanceService.changePendingIntoBalance(account,transactionRequest.getAmount());
//        accountBalanceService.removePendingBalance(account,transactionRequest.getAmount());
        var charge = new ChargeResult(transactionRequest.getAmount(),BigDecimal.ZERO,transactionRequest.getAmount());
        var exchange = ExchangeResult.builder().toCurrencyCode(CurrencyCode.USD).toExchangedAmount(transactionRequest.getAmount())
                .fromCurrencyCode(CurrencyCode.USD).exchangeRate(BigDecimal.ZERO).fromExchangedAmount(transactionRequest.getAmount()).build();
        var transactionRecord =  createTransactionRecord(charge, exchange,transactionRequest,adminAccount,account);
        var transaction = transactionRecordService.saveTransactionRecord(transactionRecord);
        return new PaymentResponse(transaction.getTransactionId(), account.getCurrencyCode().name(), charge.amountAndCharge(), transaction);
    }

    private void isAllowedToRechargeSettlement(AdminAccountResponse accountResponse){
        var allowedUserTypes = List.of(UserType.ADMIN,UserType.DEV,UserType.SUPER_ADMIN);
        if(!allowedUserTypes.contains(accountResponse.getUserType()))
            throw new ApiException(String.format("User type is %s its not allowed to recharge the settlement",accountResponse.getUserType()),HttpStatus.NOT_ACCEPTABLE);
    }

    private TransactionRecord createTransactionRecord(ChargeResult chargeResult, ExchangeResult exchangeResult,
                                                      TransactionRequest transactionRequest, AdminAccountResponse accountResponse,Account receiver){
        return  TransactionRecord.builder()
                .senderAccount(accountResponse.getId().toString()).receiverAccount(receiver.getAccountId())
                .senderName(accountResponse.getFullName())
                .transactionType(TransactionType.RECHARGE_SETTLEMENT)
                .receiverName(receiver.getAccountName())
                .creditAmount(exchangeResult.getToExchangedAmount())
                .debitAmount(chargeResult.amountAndCharge()).charge(chargeResult.charge())
                .creditCurrencyCode(exchangeResult.getToCurrencyCode()).debitCurrencyCode(exchangeResult.getFromCurrencyCode())
                .creditCurrentBalance(receiver.getBalance()).debitCurrentBalance(BigDecimal.ZERO)
                .exchangeFee(exchangeResult.getExchangeRate())
                .description(String.format("%s(%s) had made settlement recharge amount of USD %.2f and current balance is USD %.2f",accountResponse.getFullName()
                        ,accountResponse.getUserType(),chargeResult.amountAndCharge().doubleValue(),receiver.getBalance().doubleValue()))
                .transactionLocation(transactionRequest.getLocation()).transactionStatus(TransactionStatus.COMPLETED)
                .paymentChannel(PaymentChannel.INTERSPAY).build();

    }




}
