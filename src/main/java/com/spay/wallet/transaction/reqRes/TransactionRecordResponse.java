package com.spay.wallet.transaction.reqRes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TransactionRecordResponse {
    private final String transactionId;
    private final String senderMessage;
    private final String receiverMessage;
}
