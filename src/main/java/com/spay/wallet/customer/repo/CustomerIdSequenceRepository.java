package com.spay.wallet.customer.repo;

import com.spay.wallet.customer.entities.CustomerIdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerIdSequenceRepository extends JpaRepository<CustomerIdSequence, Long> {
}