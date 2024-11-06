package com.spay.wallet.transaction.repo;

import com.spay.wallet.transaction.entities.TransactionIdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionIdSequenceRepository extends JpaRepository<TransactionIdSequence, Long> {
}