package com.spay.wallet.account.repo;

import com.spay.wallet.account.entities.Account;
import com.spay.wallet.account.entities.AccountStatus;
import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountDetailsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("""
            select (count(a) > 0) from Account a inner join a.accountHolders accountHolders
            where a.accountType = ?1 and accountHolders.accountHolderId = ?2""")
    boolean existsByAccountTypeAndAccountHolders_AccountHolderId(AccountType accountType, String accountHolderId);

    @Query("""
            select a from Account a inner join a.accountHolders accountHolders
            where a.accountId = ?1 and accountHolders.accountHolderId = ?2 and accountHolders.isAdmin = true""")
    Optional<Account> findByAccountIdAndAccountHolders_AccountHolderIdAndAccountHolders_IsAdminTrue(String accountId, String accountHolderId);


    @Query("select a from Account a inner join a.accountHolders accountHolders where accountHolders.accountHolderId = :customerId")
    List<Account> findAllByAccountHolder(Long customerId);

    @Query("select a from Account a inner join a.accountHolders accountHolders where accountHolders.accountHolderId =:accountHolderId")
    List<Account> findByAccountHolders_AccountHolderId(String accountHolderId);

    @Transactional
    @Modifying
    @Query("update Account a set a.pendingBalance =:pendingBalance , a.balance =:balance where a.accountId =:accountId")
    void updatePendingBalanceAndBalanceByAccountId(BigDecimal pendingBalance, BigDecimal balance, String accountId);
    @Transactional
    @Modifying
    @Query("update Account a set a.pendingBalance =:pendingBalance  where a.accountId =:accountId")
    void updatePendingBalanceByAccountId(BigDecimal pendingBalance, String accountId);

    @Transactional
    @Modifying
    @Query("update Account a set a.accountStatus = ?1 where a.accountId = ?2")
    void updateAccountStatusByAccountId(AccountStatus accountStatus, String accountId);

    @Query("select sum(a.balance) as totalBalances, count(a.accountId) as accounts from Account a where a.currencyCode=?1")
    Optional<AccountsBalances> selectTotalBalance(CurrencyCode currencyCode);

    @Query("select (count(a) > 0) from Account a where a.accountType = ?1")
    boolean existsByAccountType(AccountType accountType);

    @Query("select a from Account a where a.accountType = ?1")
    List<Account> findByAccountType(AccountType accountType);

    @Query("""
            select (count(a) > 0) from Account a inner join a.accountHolders accountHolders
            where accountHolders.accountHolderId = ?1 and a.accountStatus in ?2 and a.accountType = ?3""")
    boolean existsByAccountHolders_AccountHolderIdAndAccountStatusInAndAccountType(String accountHolderId, Collection<AccountStatus> accountStatuses, AccountType accountType);

    interface AccountsBalances{
        Long getAccounts();
        BigDecimal getTotalBalances();
    }
}