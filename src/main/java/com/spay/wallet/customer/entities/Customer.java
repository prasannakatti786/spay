package com.spay.wallet.customer.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spay.wallet.credentials.Credential;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Customer {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(length = 50, nullable = false)
    private String firstName;
    @Column(length = 50, nullable = false)
    private String middleName;
    @Column(length = 50)
    private String lastName;
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private LocalDateTime joinAt;
    @Column(length = 40, nullable = false)
    private String country;
    @Column(length = 5, nullable = false)
    private String countryCode;
    @Column(length = 40)
    private String city;
    @Column(length = 40)
    private String postCode;
    @Column(length = 40)
    private String address;
    @Column(columnDefinition = "TEXT")
    private String profilePath;
    @Column(columnDefinition = "TEXT")
    private String documentFrontImagePath;
    @Column(columnDefinition = "TEXT")
    private String documentBackImagePath;
    @Column(columnDefinition = "TEXT")
    private String faceImagePath;
    @Column(length = 100)
    private String documentNumber;
    @Column(length = 40)
    private String referralCode;
    @Enumerated(EnumType.STRING)
    private UserDocumentType documentType;
    @Column(length = 13, unique = true)
    private String phoneNumber;
    @Column(length = 13)
    private String unConfirmedPhoneNumber;
    private Boolean isConformedEmailOrPhoneChanges;
    @Enumerated(EnumType.STRING)
    private CustomerLevel customerLevel;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(length = 50,  unique = true)
    private String email;
    @Column(length = 50)
    private String unConfirmedEmail;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "credential_fk", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "credential_fk_con"))
    private Credential credential;

}