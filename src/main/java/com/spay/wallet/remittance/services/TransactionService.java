package com.spay.wallet.remittance.services;

import com.spay.wallet.remittance.model.transaction.*;

public interface TransactionService {
    TransactionResponse getTransactionStatusByPtcnNo(String ptcnNo);

    TransactionResponse getCorrTransactionByPtcnNo(String ptcnNo);

    ApproveTransactionResponse approveTransactionByPtcnNo(String ptcnNo);

    PayTransactionResponse payTransactionByPtcnNo(String ptcnNo);

    TransactionBetweenDatesResponse getTransactionsDatesBetween(String startDate, String endDate);

    ProductDetails getTransactionProductsByProductId(Long productId);

    ValidTransactionResponse validateTransaction(Transaction transaction);

    SendTransactionResponse sendTransaction(Transaction transaction);

    TransactionChargeResponse getChargeOfTransaction(Long productCode, String destinationCountryCode, Long loanAmount);

    ReceivingTransactionResponse getReceivingTransactionByPtcnNo(String ptcnNo);
}
