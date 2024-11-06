package com.spay.wallet.credentials;

import com.spay.wallet.messages.MessageChannel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "credential")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credential {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credential_seq")
    @SequenceGenerator(name = "credential_seq", sequenceName = "credential_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(unique = true,nullable = false, length = 50)
    private String usernamePhone;
    @Column(unique = true,nullable = false, length = 50)
    private String usernameEmail;
    @Column(columnDefinition = "TEXT")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;
    private Boolean enabledTwoFactorAuth;
    @Enumerated(EnumType.STRING)
    private MessageChannel messageChannel;
    private String userId;
    @Column(length = 8)
    private String otpPhone;
    @Column(length = 8)
    private String otpEmail;
    private LocalDateTime otpExpireDatePhone;
    private LocalDateTime otpExpireDateEmail;
    private Boolean isEnabled;
    private Boolean isLocked;
    private LocalDateTime accountExpiresAt;
    private LocalDateTime credentialExpireAt;
    private Integer countFailedLoginAttempts;
    @Column(length = 20)
    private String sessionId;
}