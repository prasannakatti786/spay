package com.spay.wallet.transaction.service;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.services.QuickPayAccountService;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.dto.ChargeResult;
import com.spay.wallet.transaction.dto.ExchangeResult;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.payment.*;
import com.spay.wallet.transaction.payment.interspay.InterSpayPaymentService;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.interspay.RechargeSettlementService;
import com.spay.wallet.transaction.payment.topUp.TopUpInterSpayService;
import com.spay.wallet.transaction.payment.withdrawal.AgentWithdrawalService;
import com.spay.wallet.transaction.reqRes.AgentWithdrawalAcceptanceRequest;
import com.spay.wallet.transaction.reqRes.ExchangeResponse;
import com.spay.wallet.transaction.reqRes.ReceiverInfoRequest;
import com.spay.wallet.transaction.reqRes.TransactionRequest;
import com.spay.wallet.transaction.utilities.TransactionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final InterSpayPaymentService interSpayPaymentService;
    private final RechargeSettlementService rechargeSettlementService;
    private final ReceiverInfoService receiverInfoService;
    private final QuickPayAccountService quickPayAccountService;
    private final TopUpInterSpayService topUpInterSpayService;
    private final AgentWithdrawalService agentWithdrawalService;
    private final ChargeAndExchangeService chargeAndExchangeService;


    public PaymentResponse agentWithdrawalAcceptance(AgentWithdrawalAcceptanceRequest acceptanceRequest, String agentUserId){
        return  switch (acceptanceRequest.getSupposedStatus()){
            case COMPLETED -> agentWithdrawalService.acceptWithdrawalAgent(acceptanceRequest.getTransactionId()
                    ,acceptanceRequest.getAgentAccount(),acceptanceRequest.getAPin(),agentUserId);
            case FAILED -> agentWithdrawalService.declineWithdrawalAgent(acceptanceRequest.getTransactionId()
                    ,acceptanceRequest.getAgentAccount(),acceptanceRequest.getAPin() ,agentUserId);
            default ->  throw new ApiException("Supposed status is not implemented", HttpStatus.NOT_ACCEPTABLE);
        };
    }

    private PaymentResponse transferMoney(TransactionRequest request, String customerId) {
        if (request.getPaymentChannel().equals(PaymentChannel.INTERSPAY))
              return interSpayPaymentService.transferMoney(TransactionUtil.mapToPaymentPayload(request), customerId);
        throw new ApiException("Wrong payment method", HttpStatus.NOT_ACCEPTABLE);
    }

    private PaymentResponse rechargeSettlement(TransactionRequest transactionRequest, String adminAccountId){
        if(transactionRequest.getPaymentChannel().equals(PaymentChannel.INTERSPAY))
            return rechargeSettlementService.rechargeSettlement(transactionRequest,adminAccountId);
        throw new ApiException("Wrong payment method", HttpStatus.NOT_ACCEPTABLE);
    }


    private PaymentResponse withdrawMoney(TransactionRequest request, String customerId){
      if(request.getPaymentChannel().equals(PaymentChannel.INTERSPAY))
        return agentWithdrawalService.withdrawMoneyByAgent(request, customerId);
      throw new ApiException("Wrong payment method", HttpStatus.NOT_ACCEPTABLE);
    }

    private PaymentResponse topUp(TransactionRequest request, String customerId){
        if(request.getPaymentChannel().equals(PaymentChannel.INTERSPAY))
            return topUpInterSpayService.topUpAccountInterSpay(request, customerId);
        throw new ApiException("Wrong payment method", HttpStatus.NOT_ACCEPTABLE);
    }

    public PaymentResponse makePayment(TransactionRequest request, String customerId) {
        modifyQuickPay(request.getReceiverAccount(), customerId);
        return switch (request.getTransactionType()) {
            case INTERSPAY -> transferMoney(request, customerId);
            case RECHARGE_SETTLEMENT -> rechargeSettlement(request,customerId);
            case TOP_UP -> topUp(request,customerId);
            case WITHDRAWAL -> withdrawMoney(request,customerId);
            case EXTERSPAY ->
                    throw new ApiException("Unimplemented transaction type detected", HttpStatus.NOT_ACCEPTABLE);
        };
    }

    public ReceiverInfoResponse getReceiverInfo(ReceiverInfoRequest request) {
        return switch (request.getTransactionType()){
            case INTERSPAY ->  receiverInfoService.getReceiverInfoSpay(request.getReceiverAccount(), request.getPaymentChannel());
            case WITHDRAWAL -> receiverInfoService.getReceiverInfoWithdrawal(request.getReceiverAccount(), request.getPaymentChannel());
            default -> throw new ApiException("Unimplemented transaction type", HttpStatus.NOT_ACCEPTABLE);
        };

    }

    public ExchangeResponse getExchangeRate(BigDecimal amount, CurrencyCode fromCurrencyCode, CurrencyCode toCurrencyCode){
        return chargeAndExchangeService.exchangeResponse(amount,fromCurrencyCode,toCurrencyCode);
    }

    public ChargeResult getChargeFee(BigDecimal amount, PaymentChannel paymentChannel, TransactionType transactionType, String countryCode){
        return chargeAndExchangeService.calculateCharge(amount,paymentChannel,transactionType);
    }

    @Async
    public void modifyQuickPay(String receiverAccount, String customer) {
        quickPayAccountService.modifyLastUpdate(receiverAccount, customer);
    }

}
