package com.spay.wallet.account.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "account_holder")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(
        {
                "id"
        }
)
public class AccountHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_holder_seq")
    @SequenceGenerator(name = "account_holder_seq")
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(length = 50)
    private String accountHolderId;
    private Boolean isAdmin;
}