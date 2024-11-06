package com.spay.wallet.transaction.entities;

import com.spay.wallet.transaction.payment.PaymentChannel;
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
@Table(name = "charge_rate")
public class ChargeRate {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    private PaymentChannel paymentChannel;
    private TransactionType transactionType;
    private BigDecimal charge;

}