package com.spay.wallet.transaction.entities;

import com.spay.wallet.account.entities.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "exchange_rate")
public class ExchangeRate {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    private CurrencyCode fromCurrencyCode;
    private BigDecimal   fromExchangeRate;
    private CurrencyCode toCurrencyCode;
    private BigDecimal   toExchangeRate;
}