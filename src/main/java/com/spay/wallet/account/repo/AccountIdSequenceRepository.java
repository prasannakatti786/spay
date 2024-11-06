package com.spay.wallet.account.repo;

import com.spay.wallet.account.entities.AccountIdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountIdSequenceRepository extends JpaRepository<AccountIdSequence, Long> {
}