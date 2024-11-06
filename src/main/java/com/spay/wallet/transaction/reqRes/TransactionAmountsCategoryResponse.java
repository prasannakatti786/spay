package com.spay.wallet.transaction.reqRes;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.repo.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
public class TransactionAmountsCategoryResponse extends TransactionCurrencyResponse {
    private final List<String> dates = new ArrayList<>();
    private final List<Value> values = new ArrayList<>();

    public TransactionAmountsCategoryResponse(List<TransactionRepository.TransactionAmount> transactionAmounts, CurrencyCode currencyCode) {
        super(currencyCode);
        group(transactionAmounts);
    }

    private void group(List<TransactionRepository.TransactionAmount> transactionAmounts) {
        var labels = transactionAmounts.stream().collect(Collectors.groupingBy(TransactionRepository.TransactionAmount::getType));
        labels.forEach((transactionType, transactionAmounts2) -> {
            var  groupedDates = transactionAmounts2.stream().collect(Collectors.groupingBy(TransactionRepository.TransactionAmount::getDate));
            var amounts = new LinkedList<Double>();
            var sortedByKey =  new TreeMap<>(groupedDates);
            sortedByKey.forEach((date, transactionAmounts1) -> {
                dates.add(date);
                var amount = transactionAmounts1.stream().map(transactionAmount -> transactionAmount.getAmount().doubleValue()).reduce(Double::sum);
                amounts.add(amount.orElse(0.0));
            });
            var value = new Value(StringUtils.capitalize(transactionType.name()), amounts);
            values.add(value);
        });

    }



    @Data
    @AllArgsConstructor
    static class Value {
        private String label;
        private List<Double> amounts;
    }
}
