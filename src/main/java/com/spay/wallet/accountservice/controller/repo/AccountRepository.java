package com.spay.wallet.accountservice.controller.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account,String> {
    @Query("select (count(a) > 0) from Account a where a.msisdn = ?1")
    boolean existsByMsisdn(String msisdn);


    @Transactional
    @Modifying
    @Query("update Account a set a.spareMsisdn = ?1, a.phoneOtp = ?2, a.phoneOtpExpiresAt = ?3 where a.accountId = ?4")
    void updateSpareMsisdnAndPhoneOtpAndPhoneOtpExpiresAtByAccountId(String spareMsisdn, String phoneOtp, LocalDateTime phoneOtpExpiresAt, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.phoneOtp = ?1, a.phoneOtpExpiresAt = ?2 where a.accountId = ?3")
    void updatePhoneOtpAndPhoneOtpExpiresAtByAccountId(String phoneOtp, LocalDateTime phoneOtpExpiresAt, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.msisdn = ?1, a.spareMsisdn = ?2 where a.accountId = ?3")
    int updateMsisdnAndSpareMsisdnByAccountId(String msisdn, String spareMsisdn, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.spareAccountName = ?1 where a.accountId = ?2")
    void updateSpareAccountNameByAccountId(String spareAccountName, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.spareAccountName = ?1, a.accountName = ?2 where a.accountId = ?3")
    int updateSpareAccountNameAndAccountNameByAccountId(String spareAccountName, String accountName, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.msisdn = ?1, a.spareMsisdn = ?2, a.accountStatus = ?3 where a.accountId = ?4")
    int updateMsisdnAndSpareMsisdnAndAccountStatusByAccountId(String msisdn, String spareMsisdn, AccountStatus accountStatus, String accountId);

    @Query("select a from Account a inner join a.accountHolders accountHolders where accountHolders.accountHolderId = :accountHolderId")
    List<Account> findAllByAccountHolder(Long accountHolderId);

    @Query("""
            select a from Account a inner join a.accountHolders accountHolders
            where accountHolders.accountHolderId = ?1 and a.accountStatus in ?2""")
    List<Account> findByAccountHolders_AccountHolderIdAndAccountStatusIn(String accountHolderId, Collection<AccountStatus> accountStatuses);

    @Transactional
    @Modifying
    @Query("update Account a set a.aPin = ?1 where a.accountId = ?2")
    int updateAPinByAccountId(String aPin, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.accountLimit.lastDebitAt=?3, a.accountLimit.lastCreditAt=?3, a.accountLimit.debitedAmountToDay=?1, a.accountLimit.creditedAmountToDay=?2 where a.accountId=?4")
    void updateLastDebitAndCredits(BigDecimal debit, BigDecimal credit, LocalDateTime now, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.accountLimit.debitedAmountToDay=?1 where a.accountId=?2")
    void updateDebitedToDay(Double amount, String accountId);


    @Transactional
    @Modifying
    @Query("update Account a set a.accountLimit.creditedAmountToDay=?1 where a.accountId=?2")
    void updateCreditedToDay(Double amount, String accountId);

    @Query("select (count(a) > 0) from Account a where a.spareMsisdn = ?1")
    boolean existsBySpareMsisdn(String spareMsisdn);

    @Modifying
    @Query("update Account a set a.accountBalance.balance= (a.accountBalance.balance - ?1) where a.accountId=?2")
    void debitAccount(BigDecimal debitingAmount, String accountId);

    @Modifying
    @Query("update Account a set a.accountBalance.balance= (a.accountBalance.balance + ?1) where a.accountId=?2")
    void creditAccount(Double amount, String accountId);

    @Query("select  a.accountBalance.balance from Account a where a.accountId=?1")
    BigDecimal selectCurrentBalance(String accountId);
}
