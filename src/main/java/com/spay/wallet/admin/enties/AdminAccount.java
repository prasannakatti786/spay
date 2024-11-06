package com.spay.wallet.admin.enties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spay.wallet.credentials.Credential;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "admin_account")
public class AdminAccount {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    private String fullName;
    private String email;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ToString.Exclude
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "credential_fk", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "credential_fk_con"))
    private Credential credential;
}