package com.spay.wallet.account.repo;

import com.spay.wallet.account.entities.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {
}