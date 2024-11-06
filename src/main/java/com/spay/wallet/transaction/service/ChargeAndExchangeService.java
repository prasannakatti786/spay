package com.spay.wallet.transaction.service;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.common.WalletFormat;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.dto.ChargeResult;
import com.spay.wallet.transaction.dto.ExchangeResult;
import com.spay.wallet.transaction.entities.ChargeRate;
import com.spay.wallet.transaction.entities.ExchangeRate;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.repo.ChargeRateRepository;
import com.spay.wallet.transaction.repo.ExchangeRateRepository;
import com.spay.wallet.transaction.reqRes.ExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChargeAndExchangeService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final ChargeRateRepository chargeRateRepository;
    public List<ChargeRate> getCharges() {
        return chargeRateRepository.findAll();
    }

    public ChargeRate addChargeRate(PaymentChannel paymentChannel,TransactionType transactionType,BigDecimal charge) {
        var isExist = chargeRateRepository.existsByPaymentChannelAndTransactionType(paymentChannel,transactionType);
        if (isExist)
            throw new ApiException("Charge rate of %s with transaction type of %s is already exist".formatted(paymentChannel,transactionType),HttpStatus.NOT_ACCEPTABLE);

        if (charge.doubleValue() < 0.0 )
            throw new ApiException("Charge rate cannot be minus value", HttpStatus.NOT_ACCEPTABLE);

        var rate = ChargeRate.builder()
                .id(UUID.randomUUID())
                .paymentChannel(paymentChannel).transactionType(transactionType)
                .charge(charge).build();
        return chargeRateRepository.save(rate);
    }

    public ChargeRate updateChargeFee(UUID id, BigDecimal fee){
        var charge =  chargeRateRepository.findById(id).orElseThrow(() ->new ApiException("Charge fee not found",HttpStatus.NOT_FOUND));
        charge.setCharge(fee);
        return chargeRateRepository.save(charge);
    }
    public void deleteChargeFee(UUID id){
        chargeRateRepository.deleteById(id);
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    public ExchangeRate addExchangeRate(CurrencyCode fromCurrencyCode, BigDecimal fromExchangeRate,
                                        CurrencyCode toCurrencyCode, BigDecimal toExchangeRate) {
        var isExist = exchangeRateRepository.existsByFromCurrencyCodeAndToCurrencyCode(fromCurrencyCode, toCurrencyCode);
        if (isExist)
            throw new ApiException("Exchange rate of %s to %s is already apply please change it if you want to change"
                    .formatted(fromCurrencyCode, toCurrencyCode), HttpStatus.NOT_ACCEPTABLE);
        if (fromExchangeRate.doubleValue() < 0.0 || toExchangeRate.doubleValue() < 0.0)
            throw new ApiException("Rate cannot be minus value", HttpStatus.NOT_ACCEPTABLE);

        var rate = ExchangeRate.builder()
                .id(UUID.randomUUID())
                .fromExchangeRate(fromExchangeRate).toExchangeRate(toExchangeRate)
                .toCurrencyCode(toCurrencyCode).fromCurrencyCode(fromCurrencyCode).build();
        return exchangeRateRepository.save(rate);
    }

    public ExchangeRate updateExchangeRate(UUID id, BigDecimal fromExchangeRate, BigDecimal toExchangeRate){
        var exchangeRate =  exchangeRateRepository.findById(id).orElseThrow(() ->new ApiException("Exchange rate not found",HttpStatus.NOT_FOUND));
        exchangeRate.setToExchangeRate(toExchangeRate);
        exchangeRate.setFromExchangeRate(fromExchangeRate);
        return exchangeRateRepository.save(exchangeRate);
    }

    public void deleteExchangeRate(UUID id){
        exchangeRateRepository.deleteById(id);
    }

    public ChargeResult calculateCharge(BigDecimal amount, PaymentChannel paymentChannel, TransactionType transactionType) {
        var charge = chargeRateRepository.findByPaymentChannelAndTransactionType(paymentChannel,transactionType);
        if(charge.isEmpty())
         return new ChargeResult(amount, BigDecimal.ZERO, amount.add(BigDecimal.ZERO));
        var rate = charge.get();
        var fee = amount.subtract(amount.subtract(amount.multiply(rate.getCharge().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)).stripTrailingZeros()));
        return new ChargeResult(amount,fee,amount.add(fee));
    }

    public ExchangeResult calculateExchange(BigDecimal amount,
                                            CurrencyCode fromCurrencyCode,
                                            CurrencyCode toCurrencyCode) {

        if (fromCurrencyCode.equals(toCurrencyCode))
            return ExchangeResult.builder()
                    .message("----")
                    .exchangeRate(BigDecimal.ZERO).toExchangedAmount(amount).fromExchangedAmount(amount)
                    .fromCurrencyCode(fromCurrencyCode).toCurrencyCode(toCurrencyCode).build();

        var exchangeRate = exchangeRateRepository.findByFromCurrencyCodeAndToCurrencyCode(fromCurrencyCode, toCurrencyCode)
                .orElseThrow(() -> new ApiException("Sorry cannot exchange %s to %s".formatted(fromCurrencyCode, toCurrencyCode), HttpStatus.NOT_ACCEPTABLE));
        BigDecimal exchangingAmount = swapExchangeRate(exchangeRate, BigDecimal.valueOf(amount.doubleValue()));
        return ExchangeResult.builder()
                .message("%s %s = %s %s".formatted(exchangeRate.getFromCurrencyCode(),WalletFormat.currencyFormat(exchangeRate.getFromExchangeRate()),
                        exchangeRate.getToCurrencyCode(),WalletFormat.currencyFormat(exchangeRate.getToExchangeRate())))
                .exchangeRate(exchangeRate.getToExchangeRate().stripTrailingZeros())
                .toExchangedAmount(exchangingAmount.stripTrailingZeros())
                .fromExchangedAmount(amount)
                .fromCurrencyCode(fromCurrencyCode)
                .toCurrencyCode(toCurrencyCode)
                .build();
    }


    public ExchangeResponse exchangeResponse(BigDecimal amount,
                                             CurrencyCode fromCurrencyCode,
                                             CurrencyCode toCurrencyCode){
        var exchange = calculateExchange(amount,fromCurrencyCode,toCurrencyCode);
        return  ExchangeResponse.builder()
                .message(exchange.getMessage())
                .exchangeRate(WalletFormat.currencyFormat(exchange.getExchangeRate()))
                .toExchangedAmount(WalletFormat.currencyFormat(exchange.getToExchangedAmount()))
                .fromExchangedAmount(WalletFormat.currencyFormat(exchange.getFromExchangedAmount()))
                .fromCurrencyCode(fromCurrencyCode)
                .toCurrencyCode(toCurrencyCode)
                .build();
    }

    private BigDecimal swapExchangeRate(ExchangeRate exchangeRate, BigDecimal exchangingAmount) {
        var amount = exchangingAmount.doubleValue()*exchangeRate.getToExchangeRate().doubleValue();
        var result  = amount / exchangeRate.getFromExchangeRate().doubleValue();
        return  BigDecimal.valueOf(result);
    }
}
