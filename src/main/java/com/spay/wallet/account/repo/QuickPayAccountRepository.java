package com.spay.wallet.account.repo;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.QuickPayAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface QuickPayAccountRepository extends JpaRepository<QuickPayAccount, Long> {
    @Query("select (count(q) > 0) from QuickPayAccount q where q.account = ?1 and q.customerId = ?2")
    boolean existsByAccountAndCustomerId(String account, String customerId);

    @Query("select q from QuickPayAccount q where q.customerId = ?1 and q.currencyCode = ?2")
    List<QuickPayAccount> findByCustomerIdAndCurrencyCode(String customerId, CurrencyCode currencyCode, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from QuickPayAccount q where q.customerId = ?1 and q.account = ?2")
    void deleteByCustomerIdAndAccount(String customerId, String account);

    @Transactional
    @Modifying
    @Query("update QuickPayAccount q set q.lastModify = ?1 where q.account = ?2 and q.customerId = ?3")
    void updateLastModifyByAccountAndCustomerId(LocalDateTime lastModify, String account, String customerId);
}