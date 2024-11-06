package com.spay.wallet.transaction.repo;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.entities.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {
    @Query("select (count(e) > 0) from ExchangeRate e where e.fromCurrencyCode = ?1 and e.toCurrencyCode = ?2")
    boolean existsByFromCurrencyCodeAndToCurrencyCode(CurrencyCode fromCurrencyCode, CurrencyCode toCurrencyCode);

    @Query("select e from ExchangeRate e where e.fromCurrencyCode = ?1 and e.toCurrencyCode = ?2")
    Optional<ExchangeRate> findByFromCurrencyCodeAndToCurrencyCode(CurrencyCode fromCurrencyCode, CurrencyCode toCurrencyCode);
}