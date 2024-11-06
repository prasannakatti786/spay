package com.spay.wallet.transaction.reqRes;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.repo.TransactionRepository;
import com.spay.wallet.common.WalletFormat;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ProfitByCategoryResponse extends TransactionCurrencyResponse{
   private final List<TransactionType> transactionTypes = new ArrayList<>();
   private final List<String> amounts = new ArrayList<>();

   public ProfitByCategoryResponse(List<TransactionRepository.ProfitsByCategory> profitsByCategories, CurrencyCode currencyCode){
       super(currencyCode);
       profitsByCategories.forEach(profitsByCategory -> {
            transactionTypes.add(profitsByCategory.getType());
            amounts.add(profitsByCategory.getAmount().toString());
         });
   }


}
