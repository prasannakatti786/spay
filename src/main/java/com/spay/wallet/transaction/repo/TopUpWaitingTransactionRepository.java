package com.spay.wallet.transaction.repo;

import com.spay.wallet.transaction.entities.TopUpWaitingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopUpWaitingTransactionRepository extends JpaRepository<TopUpWaitingTransaction, Long> {
    @Query("select t from TopUpWaitingTransaction t where t.receiverAccount = ?1")
    List<TopUpWaitingTransaction> findByReceiverAccount(String receiverAccount);
}