package com.spay.wallet.remittance.model.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ApiTransaction {

    private String ptcnNo;
    private String fAmount;
    private String satus;
    private String satusDescription;
    private String sbmittedDate;
    private String cstomerName;
    private String bneName;
    private String rmarks1;
    private String rmarks;
}
