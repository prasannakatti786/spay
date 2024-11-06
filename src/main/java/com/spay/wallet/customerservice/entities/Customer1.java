package com.spay.wallet.customerservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Customer1")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Customer1 {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String middleName;
    @Column(length = 50)
    private String lastName;
    private LocalDate dateOfBirth;
    @Column(nullable = false, columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime joinAt;
    @Column(length = 40)
    private String country;
    @Column(length = 5)
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
    private CustomerDocumentType documentType;
    @Column(length = 30, unique = true)
    private String phoneNumber;
    @Column(length = 30)
    private String unConfirmedPhoneNumber;
    private Boolean isConformedEmailOrPhoneChanges;
    @Enumerated(EnumType.STRING)
    private CustomerLevel1 customerLevel;
    @Enumerated(EnumType.STRING)
    private Gender1 gender;
    @Column(length = 50,  unique = true)
    private String email;
    @Column(length = 50)
    private String unConfirmedEmail;
    @Column(length = 250,  unique = true, nullable = false)
    private String credentialId;
}