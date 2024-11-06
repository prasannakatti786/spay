package com.spay.wallet.customerservice.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customerIdSequence1")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerIdSequence1 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private Long currentId;

}
