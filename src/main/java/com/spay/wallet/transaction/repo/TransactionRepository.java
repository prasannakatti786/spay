package com.spay.wallet.transaction.repo;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.entities.Transaction;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.entities.TransactionType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

     @Query("""
             select t from Transaction t
             where t.senderAccount = ?1 or t.receiverAccount = ?2
             order by t.transactionDate DESC""")
     List<Transaction> findBySenderAccountOrReceiverAccountOrderByTransactionDateDesc(String senderAccount, String receiverAccount, Pageable pageable);

     @Query("""
             select t from Transaction t
             where t.senderAccount = ?1 or t.receiverAccount = ?2 and t.transactionDate between ?3 and ?4
             order by t.transactionDate DESC""")
     List<Transaction> findBySenderAccountOrReceiverAccountAndTransactionDateBetweenOrderByTransactionDateDesc(String senderAccount, String receiverAccount, LocalDateTime transactionDateStart, LocalDateTime transactionDateEnd);

     @Query("""
             select distinct t from Transaction t
             where (t.senderAccount = ?1 or t.receiverAccount = ?1) and (t.receiverAccount = ?2 or t.senderAccount = ?2) and t.transactionDate between ?3 and ?4  order by t.transactionDate DESC""")
     List<Transaction> findDistinctBySenderAccountOrReceiverAccountOrReceiverAccountOrSenderAccountAndTransactionDateBetween(String senderAccount, String receiverAccount, LocalDateTime transactionDateStart, LocalDateTime transactionDateEnd);

     @Query("""
             select t from Transaction t
             where t.senderAccount = ?1 and t.receiverAccount = ?2 and t.transactionDate >= ?3 and t.transactionDate <= ?4
             order by t.transactionDate DESC""")
     List<Transaction> findBySenderAccountAndReceiverAccountAndTransactionDateLessThanEqualAndTransactionDateGreaterThanEqualOrderByTransactionDateDesc(String senderAccount, String receiverAccount, LocalDateTime transactionDate, LocalDateTime transactionDate1);

     @Query("""
             select t from Transaction t
             where (t.senderAccount = ?1 or t.receiverAccount = ?2) AND  t.transactionDate BETWEEN  ?3 and ?4
             order by t.transactionDate  DESC""")
     List<Transaction> findBySenderAccountAndReceiverAccountAndTransactionDateAfterAndTransactionDateBeforeOrderByTransactionDate(String senderAccount, String receiverAccount, LocalDateTime transactionDate, LocalDateTime transactionDate1);

     List<Transaction> findByTransactionDateBetweenAndSenderAccountOrReceiverAccount(LocalDateTime start, LocalDateTime end, String accountId, String accountId1);


     @Query("select t.transactionType as type,count(t) AS total from Transaction t group by t.transactionType")
     List<TransactionCategoryCount> countTransactionByCategory();

     @Query("select t.transactionType as type, sum(t.charge) AS amount from Transaction t where  (t.creditCurrencyCode=:code or t.debitCurrencyCode=:code) and  t.transactionDate between :start and :end group by t.transactionType")
     List<ProfitsByCategory> sumProfitsByCategory(LocalDateTime start, LocalDateTime end, CurrencyCode code);

     @Query("select t.transactionType as type, HOUR( t.transactionDate) as date, t.debitAmount as amount from Transaction t WHERE " +
             "t.debitCurrencyCode=:code and t.transactionDate between :start and :end")
     List<TransactionAmount> transactionsByHour(LocalDateTime start, LocalDateTime end, CurrencyCode code);

     @Query("select t.transactionType as type, DAY( t.transactionDate) as date, t.debitAmount as amount from Transaction t WHERE " +
             "t.debitCurrencyCode=:code and t.transactionDate between :start and :end")
     List<TransactionAmount> transactionsByDay(LocalDateTime start, LocalDateTime end, CurrencyCode code);

     @Query("select t from Transaction t where t.transactionType = ?1")
     Page<Transaction> findAllByTransactionType(TransactionType transactionType, Pageable pageable);

     @Query("""
             select t from Transaction t
             where upper(t.transactionId) like upper(?1) or upper(t.senderAccount) like upper(?1) or upper(t.receiverAccount) like upper(?1)""")
     List<Transaction> findByTransactionIdLikeIgnoreCaseOrSenderAccountLikeIgnoreCaseOrReceiverAccountLikeIgnoreCase(String key);

     @Query("select t from Transaction t where t.transactionType = ?1 and (t.senderAccount=?2 or t.receiverAccount=?2)")
     Page<Transaction> findAllByTransactionTypeAndAccount(TransactionType type, String accountNo, Pageable pageable);
     @Query("select t from Transaction t where t.senderAccount=?1 or t.receiverAccount=?1")
     Page<Transaction> findAllByAccount(String accountNo, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Transaction t set t.transactionStatus = ?1 where t.transactionId = ?2")
    void updateTransactionStatusByTransactionId(TransactionStatus transactionStatus, String transactionId);

    @Query("select t from Transaction t where t.receiverAccount = ?1")
    List<Transaction> findByReceiverAccount(String receiverAccount);

    @Query("select t from Transaction t where t.receiverAccount = ?1 and t.transactionStatus = ?2")
    List<Transaction> findByReceiverAccountAndTransactionStatus(String receiverAccount, TransactionStatus transactionStatus);

    @Query("""
            select t from Transaction t
            where t.receiverAccount = ?1 and t.transactionStatus = ?2 and t.transactionType = ?3""")
    List<Transaction> findByReceiverAccountAndTransactionStatusAndTransactionType(String receiverAccount, TransactionStatus transactionStatus, TransactionType transactionType);

    interface TransactionCategoryCount{
         TransactionType getType();
         Long getTotal();
     }

     interface TransactionAmount{
          TransactionType getType();
          String getDate();

          BigDecimal getAmount();
     }

     interface ProfitsByCategory{
          TransactionType getType();
          BigDecimal getAmount();
     }


}
