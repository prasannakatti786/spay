package com.spay.wallet.account.entities;


import com.spay.wallet.account.entities.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "quick_pay_account")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class QuickPayAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quick_pay_account_seq")
    @SequenceGenerator(name = "quick_pay_account_seq", sequenceName = "quick_pay_account_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String account;
    private String customerId;
    private String accountName;
    private String accountAdminId;
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;
    private LocalDateTime lastModify;

}
