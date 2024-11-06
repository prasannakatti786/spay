package com.spay.wallet.transaction.reqRes;


import com.spay.wallet.transaction.repo.TransactionRepository;
import lombok.Data;

import java.util.List;

@Data
public class TransactionTypeCountResponse {
    private Long totalDeposits = 0L;
    private Long totalWithdrawals=0L;
    private Long totalTransfers=0L;
    public TransactionTypeCountResponse(List<TransactionRepository.TransactionCategoryCount> transactionCategoryCounts){
        transactionCategoryCounts.forEach(transactionCategoryCount->{
           switch (transactionCategoryCount.getType()){
               case TOP_UP -> totalDeposits = transactionCategoryCount.getTotal();
               case EXTERSPAY, INTERSPAY -> totalTransfers = transactionCategoryCount.getTotal();
               case WITHDRAWAL -> totalWithdrawals =  transactionCategoryCount.getTotal();
           }
        });
    }
}
