package com.spay.wallet.account.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "accountIdSequence")
@NoArgsConstructor
@AllArgsConstructor
public class AccountIdSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private Long currentId;
    private String lastId;
}
