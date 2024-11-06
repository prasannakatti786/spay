package com.spay.wallet.account.services;

import com.spay.wallet.account.entities.*;
import com.spay.wallet.account.repo.AccountRepository;
import com.spay.wallet.account.reqAndRes.accountCreation.BalanceQueryResponse;
import com.spay.wallet.common.WalletFormat;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.transaction.payment.PaymentPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountBalanceService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public BalanceQueryResponse queryBalance(Long customerId, String accountId) {
        var account = getAccount(accountId, customerId);
        if (account.getAccountStatus().equals(AccountStatus.DELETE))
            throw new ApiException("This account has been deleted", HttpStatus.NOT_FOUND);
        if (account.getAccountStatus().equals(AccountStatus.BLACKLISTED))
            throw new ApiException("This account has been in blocked, please contact the support team.", HttpStatus.NOT_FOUND);
        return mapAccountToBalanceResponse(account);
    }

    public BalanceQueryResponse adminQueryBalance( String accountId) {
        var account = getAccount(accountId);
        return mapAccountToBalanceResponse(account);
    }

    public List<BalanceQueryResponse> queryBalances(Long customerId) {
        var accounts = getAccountByAccountHolder(customerId);
        return accounts.stream()
                .filter(account -> !List.of(AccountStatus.DELETE, AccountStatus.BLACKLISTED).contains(account.getAccountStatus()))
                .map(this::mapAccountToBalanceResponse).collect(Collectors.toList());
    }


    private BalanceQueryResponse mapAccountToBalanceResponse(Account account) {
        return BalanceQueryResponse.builder()
                .accountNumber(account.getAccountId())
                .currentBalance(convertBigDecToDouble(account.getBalance()))
                .pendingBalance(convertBigDecToDouble(account.getPendingBalance()))
                .currencyCode(account.getCurrencyCode())
                .currencyName(account.getCurrencyName())
                .accountType(account.getAccountType())
                .shadowBalance(account.getShadowBalance().doubleValue())
                .currencyNumericCode(account.getCurrencyNumericCode())
                .currencySymbol(account.getCurrencySymbol())
                .currencyType(account.getCurrencyType())
                .build();
    }

    private static Double convertBigDecToDouble(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_DOWN).stripTrailingZeros().doubleValue();
    }

    private List<Account> getAccountByAccountHolder(Long customerId) {
        return accountRepository.findByAccountHolders_AccountHolderId(customerId.toString());
    }

    public Account getAccount(String accountId, Long customerId) {
        var account = accountRepository.findById(accountId).orElseThrow(() -> new ApiException("Account does not exist", HttpStatus.NOT_FOUND));
        if (!isAccountHolder(customerId.toString(), account.getAccountHolders()))
            throw new ApiException("You are not authorized to this account", HttpStatus.NOT_ACCEPTABLE);
        return account;
    }



    public Account getAccount(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new ApiException("Account does not exist", HttpStatus.NOT_FOUND));
    }


    private Boolean isAccountHolder(String accountId, List<AccountHolder> accountHolders) {
        return accountHolders.stream().anyMatch(accountHolder -> accountHolder.getAccountHolderId().equals(accountId));
    }

    public void changeBalanceIntoPending(Account account, BigDecimal amount) {
        isEnoughBalance(account, amount);
        account.setBalance(account.getBalance().subtract(amount));
        account.setPendingBalance(account.getPendingBalance().add(amount));
        accountRepository.updatePendingBalanceAndBalanceByAccountId(account.getPendingBalance(), account.getBalance(), account.getAccountId());
    }

    public void addPendingBalance(Account account, BigDecimal amount) {
        account.setPendingBalance(account.getPendingBalance().add(amount));
        accountRepository.updatePendingBalanceByAccountId(account.getPendingBalance(), account.getAccountId());
    }

    public void removePendingBalance(Account account, BigDecimal amount) {
        isEnoughPendingBalance(account, amount);
        account.setPendingBalance(account.getPendingBalance().subtract(amount));
        accountRepository.updatePendingBalanceByAccountId(account.getPendingBalance(), account.getAccountId());
    }


    public void changePendingIntoBalance(Account account, BigDecimal amount) {
        isEnoughPendingBalance(account, amount);
        account.setPendingBalance(account.getPendingBalance().subtract(amount));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.updatePendingBalanceAndBalanceByAccountId(account.getPendingBalance(), account.getBalance(), account.getAccountId());
    }

    public void isEnoughBalance(Account account, BigDecimal amount) {
        if (account.getBalance().doubleValue() < amount.doubleValue())
            throw new ApiException("Insufficient balance", HttpStatus.NOT_ACCEPTABLE);
    }

    public void isEnoughPendingBalance(Account account, BigDecimal amount) {
        if (account.getPendingBalance().doubleValue() < amount.doubleValue())
            throw new ApiException("Insufficient balance", HttpStatus.NOT_ACCEPTABLE);
    }

    public void isAccountSettlementAccount(Account account){
        if(account.getAccountType().equals(AccountType.SETTLEMENT_ACCOUNT))
            throw new ApiException("Cannot transfer money to settlement account", HttpStatus.NOT_ACCEPTABLE);
    }

    public void isAccountAllowedForReceiving(Account account) {
        var statuses = List.of(AccountStatus.ACTIVE, AccountStatus.LOST, AccountStatus.BLACKLISTED);
        isAccountSettlementAccount(account);
        if (!statuses.contains(account.getAccountStatus()))
            throw new ApiException("This account is not allowed to receive transaction", HttpStatus.NOT_ACCEPTABLE);
    }

    public void isAccountAllowedForSending(Account account) {
        var statuses = List.of(AccountStatus.ACTIVE);
        if (!statuses.contains(account.getAccountStatus()))
            throw new ApiException("This account is not allowed to send transaction", HttpStatus.NOT_ACCEPTABLE);
    }

    public void checkTypeOfCurrency(Account account, CurrencyCode currencyCode) {
        if (!account.getCurrencyCode().equals(currencyCode))
            throw new ApiException(String.format("The currency of %s is not same as %s", account.getCurrencyCode(), currencyCode), HttpStatus.NOT_ACCEPTABLE);
    }

    public void checkAPIN(Account account, String aPin) {
        if (!passwordEncoder.matches(aPin, account.getAPin()))
            throw new ApiException("APIN is invalid", HttpStatus.NOT_ACCEPTABLE);
    }

    public void isAllowedToWithdrawOrTransferMoney(Account account){
        var notAllowed = List.of(AccountType.SETTLEMENT_ACCOUNT,AccountType.AGENT_ACCOUNT);
        if(notAllowed.contains(account.getAccountType()))
            throw new ApiException("This account is not allowed to withdraw/transfer money please use allow process",HttpStatus.NOT_ACCEPTABLE);
    }

    public void validateCommonPredicates(Account senderAccount, PaymentPayload payload) {
        checkAPIN(senderAccount, payload.getAPin());
        checkIsSameAccount(payload.getSenderAccount(), payload.getReceiverAccount());
        isZeroAmount(payload.getAmount());
        isMoreThen2Dec(payload.getCurrencyCode(), payload.getAmount());
        checkDuplicatePaymentRequest(senderAccount, payload);
    }
    public void isZeroAmount(BigDecimal amount) {
        if (amount.doubleValue() <= 0)
            throw new ApiException("Cannot send zero amount or less then zero", HttpStatus.NOT_ACCEPTABLE);
    }

    public void isMoreThen2Dec(String code, BigDecimal amount) {
        var currencyCode = CurrencyCode.valueOf(code);
        var type = currencyCode.getCurrencyType();
        if (!List.of(CurrencyType.FIAT, CurrencyType.CREDIT).contains(type))
            return;
        if (!amount.toString().contains(".")) return;
        var amounts = amount.toString().split("\\.");
        var afterDecimalPoint = amounts[1];
        if (StringUtils.isEmpty(afterDecimalPoint)) return;
        if (afterDecimalPoint.length() == 1) return;
        if (StringUtils.isEmpty(afterDecimalPoint.substring(2))) return;
        var afterTwo = Long.parseLong(afterDecimalPoint.substring(2));
        if (afterTwo > 0)
            throw new ApiException("Two digits is allowed after decimal point", HttpStatus.NOT_ACCEPTABLE);
    }

    public void checkIsAccountHolder(Account senderAccount, String customerId) {
        if (!isAccountHolder(customerId, senderAccount.getAccountHolders()))
            throw new ApiException("You are not authorized to this account", HttpStatus.NOT_ACCEPTABLE);
    }

    public void checkIsSameAccount(String senderAccount,String receiverAccount) {
        if (senderAccount.equals(receiverAccount))
            throw new ApiException("Sander and receiver cannot be same account", HttpStatus.NOT_ACCEPTABLE);
    }



    public void checkDuplicatePaymentRequest(Account account, PaymentPayload payload) {
        var isDuplicate = isDuplicatePayment(account, payload, payload.getAmount());
        if (!isDuplicate) {
            updateLastDebit(account, payload);
            return;
        }
        log.warn("Duplicate payment is detected account {} and payload {}", account, payload);
        throw new ApiException("Duplicate request detected", HttpStatus.NOT_ACCEPTABLE);
    }

    private void updateLastDebit(Account account, PaymentPayload payload) {
        account.setLastDebitAmount(payload.getAmount());
        account.setLastDebitTransactionType(payload.getTransactionType());
        account.setLastDebitPaymentChannel(payload.getPaymentChannel());
        account.setLestDebitAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private Boolean isDuplicatePayment(Account account, PaymentPayload payload, BigDecimal amount) {
        if (!payload.getTransactionType().equals(account.getLastDebitTransactionType()))
            return Boolean.FALSE;
        if (LocalDateTime.now().minusSeconds(10).isAfter(account.getLestDebitAt()))
            return Boolean.FALSE;
        return Boolean.TRUE;
    }


    public void isAccountExist(String otherAccount) {
        if(!accountRepository.existsById(otherAccount))
            throw new ApiException("Account of "+otherAccount+" is not exist",HttpStatus.NOT_FOUND);
    }

    public Map<String, Object> getTotalAccountsBalance(CurrencyCode currencyCode) {
     var accountBalances = accountRepository.selectTotalBalance(currencyCode);
        return accountBalances.<Map<String, Object>>map(accountsBalances ->
                Map.of("totalBalances", WalletFormat.currencyFormat(accountsBalances.getTotalBalances()), "accounts", accountsBalances.getAccounts()))
                .orElseGet(() -> Map.of("totalBalances", WalletFormat.currencyFormat(BigDecimal.ZERO), "accounts", 0));
    }
}
