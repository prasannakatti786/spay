package com.spay.wallet.transaction.reqRes;

import com.spay.wallet.transaction.entities.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AgentWithdrawalAcceptanceRequest {
    @NotNull(message = "Agent account is required")
    private final String agentAccount;
    @NotNull(message = "Transaction Id is required")
    private final Long transactionId;
    @NotNull(message = "Supposed status is required")
    private final TransactionStatus supposedStatus;
    @NotNull(message = "A-PIN is required")
    private final String aPin;
}
