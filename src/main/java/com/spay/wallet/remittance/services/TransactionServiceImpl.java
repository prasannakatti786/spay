package com.spay.wallet.remittance.services;

import com.spay.wallet.remittance.model.ApiConfig;
import com.spay.wallet.remittance.model.transaction.*;
import com.spay.wallet.common.RemittanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TransactionServiceImpl implements TransactionService {

    private static Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    RemittanceUtil remittanceUtil;

    @Autowired
    ApiConfig apiConfig;

    @Override
    public TransactionResponse getTransactionStatusByPtcnNo(String ptcnNo) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String transactionStatusUri = apiConfig.getBaseUrl() + "/api/v1/transaction_status/" + ptcnNo;
        return (TransactionResponse) remittanceUtil.getApiResponse(HttpMethod.GET, transactionStatusUri, tokenResponse.getToken(), TransactionResponse.class);
    }

    @Override
    public TransactionResponse getCorrTransactionByPtcnNo(String ptcnNo) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String transactionStatusUri = apiConfig.getBaseUrl() + "/api/v1/corr_transaction/" + ptcnNo;
        return (TransactionResponse) remittanceUtil.getApiResponse(HttpMethod.GET, transactionStatusUri, tokenResponse.getToken(), TransactionResponse.class);
    }

    @Override
    public ApproveTransactionResponse approveTransactionByPtcnNo(String ptcnNo) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String approveTransactionUri = apiConfig.getBaseUrl() + "/api/v1/approve_transaction/" + ptcnNo;
        return (ApproveTransactionResponse) remittanceUtil.patchApiResponse(approveTransactionUri, tokenResponse.getToken(), ApproveTransactionResponse.class);
    }

    @Override
    public PayTransactionResponse payTransactionByPtcnNo(String ptcnNo) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String payTransactionUri = apiConfig.getBaseUrl() + "/api/v1/pay_transaction/" + ptcnNo;
        return (PayTransactionResponse) remittanceUtil.patchApiResponse(payTransactionUri, tokenResponse.getToken(), PayTransactionResponse.class);
    }

    @Override
    public TransactionBetweenDatesResponse getTransactionsDatesBetween(String startDate, String endDate) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String transactionsBetweenDatesUri = apiConfig.getBaseUrl() + "/api/v1/transactions/date_between?startDate=" + startDate + "&endDate=" + endDate;
        return (TransactionBetweenDatesResponse) remittanceUtil.getApiResponse(HttpMethod.GET, transactionsBetweenDatesUri, tokenResponse.getToken(), TransactionBetweenDatesResponse.class);
    }

    @Override
    public ProductDetails getTransactionProductsByProductId(Long productId) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String getRequiredFieldsByProductId = apiConfig.getBaseUrl() + "/api/v1/transactions/products/get_required_fields/" + productId;
        return (ProductDetails) remittanceUtil.getApiResponse(HttpMethod.GET, getRequiredFieldsByProductId, tokenResponse.getToken(), ProductDetails.class);
    }

    @Override
    public ValidTransactionResponse validateTransaction(Transaction transaction) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String validateTransaction = apiConfig.getBaseUrl() + "/api/v1/validate_transaction";
        return (ValidTransactionResponse) remittanceUtil.postApiResponse(transaction, validateTransaction, tokenResponse.getToken(), ValidTransactionResponse.class);
    }

    @Override
    public SendTransactionResponse sendTransaction(Transaction transaction) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String sendTransactionUri = apiConfig.getBaseUrl() + "/api/v1/send_transaction";
        return (SendTransactionResponse) remittanceUtil.postApiResponse(transaction, sendTransactionUri, tokenResponse.getToken(), SendTransactionResponse.class);
    }

    @Override
    public TransactionChargeResponse getChargeOfTransaction(Long productCode, String destinationCountryCode, Long localAmount) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String getChargeOfTransactionUri = apiConfig.getBaseUrl() + "/api/v1/transactions/get_charge?product_code=" + productCode + "&destination_country_code=" + destinationCountryCode + "&local_amount=" + localAmount;
        return (TransactionChargeResponse) remittanceUtil.getApiResponse(HttpMethod.GET, getChargeOfTransactionUri, tokenResponse.getToken(), TransactionChargeResponse.class);
    }

    @Override
    public ReceivingTransactionResponse getReceivingTransactionByPtcnNo(String ptcnNo) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String receivingTransactionUri = apiConfig.getBaseUrl() + "/api/v1/receiving_transaction/" + ptcnNo;
        return (ReceivingTransactionResponse) remittanceUtil.getApiResponse(HttpMethod.GET, receivingTransactionUri, tokenResponse.getToken(), ReceivingTransactionResponse.class);
    }

}
