package com.spay.wallet.remittance.model.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    private String payinReferenceNo;
    private String transferCountryCode;
    private String beneCurrencyCode;
    private int transferType;
    private int productCode;
    private int payingAgentCode;
    private int subAgentCode;
    private CustomerAttributes customerAttributes;
    private BeneficiaryAttributes beneficiaryAttributes;
    private String beneAccountTypeId;
    private String accountName;
    private String accountNo;
    private int bankCode;
    private String bankName;
    private int branchCode;
    private String branchName;
    private String branchBankAssignCode;
    private String branchPrimaryAddress;
    private String branchSecondaryAddress;
    private String branchCity;
    private int pickupCenterCode;
    private String areaCode;
    private String backendCharge;
    private int transferAmount;
    private int usdSetRate;
    private int usdSetCharge;
    private int usdSetAmount;
    private int fclcRate;
    private int localAmount;
    private int purposeOfTransferCode;
    private int sourceOfIncomeCode;
    private int idTypeCode;
    private String remarks1;
    private String remarks2;
}