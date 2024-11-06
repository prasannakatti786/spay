package com.spay.wallet.transaction.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transactionIdSequence")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionIdSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private Long currentId;
}
