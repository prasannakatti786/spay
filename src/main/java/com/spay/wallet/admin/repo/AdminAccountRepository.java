package com.spay.wallet.admin.repo;

import com.spay.wallet.admin.enties.AdminAccount;
import com.spay.wallet.credentials.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, UUID> {
    @Query("select (count(a) > 0) from AdminAccount a where a.credential.userType = ?1")
    boolean existsByCredential_UserType(UserType userType);

    @Query("select (count(a) > 0) from AdminAccount a where a.email = ?1")
    boolean existsByEmail(String email);
}