package com.spay.wallet.account.services;

import com.spay.wallet.account.entities.*;
import com.spay.wallet.account.repo.AccountHolderRepository;
import com.spay.wallet.account.repo.AccountRepository;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountDetailsResponse;
import com.spay.wallet.account.reqAndRes.accountCreation.BalanceQueryResponse;
import com.spay.wallet.account.utilities.FormatDateAndTime;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.repo.CustomerRepository;
import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final CustomerRepository customerRepository;


    public List<AccountDetailsResponse> getAccounts(Long customerId) {
        return accountRepository.findAllByAccountHolder(customerId)
                .stream()
                .filter(account -> !List.of(AccountStatus.DELETE, AccountStatus.BLACKLISTED).contains(account.getAccountStatus()))
                .sorted(Comparator.comparing(Account::getAccountId))
                .map(account -> mapAccountDetails(customerId, account)).toList();
    }

    public List<AccountDetailsResponse> getAccountsByAdmin(Long customerId) {
        return accountRepository.findAllByAccountHolder(customerId)
                .stream()
                .map(account -> mapAccountDetails(customerId, account)).toList();
    }

    private  AccountDetailsResponse mapAccountDetails(Long customerId, Account account) {
        return AccountDetailsResponse.builder()
                .accountNumber(account.getAccountId())
                .accountHolders(account.getAccountHolders())
                .accountType(account.getAccountType())
                .accountStatus(account.getAccountStatus())
                .accountName(account.getAccountName())
                .currencyCode(account.getCurrencyCode())
                .currencyType(account.getCurrencyType())
                .hasAPin(account.getAPin() != null)
                .currencyName(account.getCurrencyName())
                .createdAt(FormatDateAndTime.formatDateTime(account.getCreatedAt()))
                .customerId(customerId.toString())
                .build();
    }


    public void changeAccountStatus(Long customerId,String accountId, AccountStatus accountStatus){
        var account =  getAccount(accountId,customerId);
        var accountHolder = getAccountHolder(customerId.toString(),account.getAccountHolders());
        if(!accountHolder.getIsAdmin())
            throw new ApiException("You are not authorized to this action",HttpStatus.NOT_ACCEPTABLE);
        if(account.getAccountStatus().equals(AccountStatus.DELETE))
            throw new ApiException("This account does not exist",HttpStatus.NOT_FOUND);
        if(account.getAccountStatus().equals(AccountStatus.LOST))
            throw new ApiException("This account is already reported lost",HttpStatus.NOT_FOUND);
        if(accountStatus.equals(AccountStatus.DELETE) &&  account.getShadowBalance().add(account.getBalance()).add(account.getPendingBalance()).doubleValue() > 0)
            throw new ApiException("This account Can not be deleted",HttpStatus.NOT_ACCEPTABLE);
        account.setAccountStatus(accountStatus);
        accountRepository.save(account);
    }

    public List<Customer> getAccountHolder(String accountId){
        var account = getAccount(accountId);
        var accountHolder = account.getAccountHolders().stream().map(AccountHolder::getAccountHolderId)
                .filter(StringUtils::isNumeric)
                .map(Long::parseLong).toList();
        return customerRepository.findAllById(accountHolder);
    }



    public void swapAdmin(String accountId, Long customerId,Long toCustomerId){
        if(Objects.equals(toCustomerId, customerId))
            throw new ApiException("Is not allowed to assign by yourself",HttpStatus.NOT_ACCEPTABLE);
        var account = getAccount(accountId, customerId);
        var accountHolders=  account.getAccountHolders();
        if(!isAccountHolder(toCustomerId.toString(),accountHolders))
            throw new ApiException("This customer is not beneficiary to this account",HttpStatus.NOT_ACCEPTABLE);
        var myAccount = getAccountHolder(customerId.toString(),accountHolders);
        var toAccount = getAccountHolder(toCustomerId.toString(),accountHolders);
        if(!myAccount.getIsAdmin())
            throw new ApiException("You are not authorized",HttpStatus.NOT_ACCEPTABLE);
        if(!account.getAccountStatus().equals(AccountStatus.ACTIVE))
            throw new ApiException("Account is not active",HttpStatus.NOT_ACCEPTABLE);
        if(toAccount.getIsAdmin())
            throw new ApiException("This customer is already bean admin", HttpStatus.NOT_ACCEPTABLE);
        toAccount.setIsAdmin(Boolean.TRUE);
        myAccount.setIsAdmin(Boolean.FALSE);
        accountHolderRepository.save(toAccount);
        accountHolderRepository.save(myAccount);
    }



    private Account getAccount(String accountId, Long customerId) {
        var account = accountRepository.findById(accountId).orElseThrow(() -> new ApiException("Account does not exist", HttpStatus.NOT_FOUND));
        if (!isAccountHolder(customerId.toString(), account.getAccountHolders()))
            throw new ApiException("You are not authorized to this account", HttpStatus.NOT_ACCEPTABLE);
        return account;
    }


    private Account getAccount(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new ApiException("Account does not exist", HttpStatus.NOT_FOUND));
    }


    private Boolean isAccountHolder(String userId, List<AccountHolder> accountHolders) {
        return accountHolders.stream().anyMatch(accountHolder -> accountHolder.getAccountHolderId().equals(userId));
    }

    private AccountHolder getAccountHolder(String customerId, List<AccountHolder> accountHolders) {
        return accountHolders.stream().filter(accountHolder -> accountHolder.getAccountHolderId().equals(customerId))
                .findFirst().orElseThrow(() -> new ApiException("You are not beneficiary to this account",HttpStatus.NOT_ACCEPTABLE));
    }


    public Account isAccountExist(String account) {
           return getAccount(account);
    }

    public void changeAccountStatus(String accountNumber,AccountStatus accountStatus){
        var account = getAccount(accountNumber);
        account.setAccountStatus(accountStatus);
        accountRepository.updateAccountStatusByAccountId(accountStatus,accountNumber);
    }


    public AccountDetailsResponse getSettlementAccount() {
        return accountRepository.findByAccountType(AccountType.SETTLEMENT_ACCOUNT).stream().map(account->mapAccountDetails(0L,account)).findFirst()
                .orElseThrow(() -> new ApiException("Settlement account is not found",HttpStatus.NOT_FOUND));

    }

    public List<CustomerDetailResponse> getCustomersByIds(String customerId, String accountId) {
        var account = getAccount(accountId);
        var isAccountHolder = isAccountHolder(customerId,account.getAccountHolders());
        if(!isAccountHolder)
            throw new ApiException("You are not beneficiary to this account",HttpStatus.NOT_ACCEPTABLE);
        var accountHolders = account.getAccountHolders().stream().map(accountHolder -> Long.parseLong(accountHolder.getAccountHolderId())).toList();
        return getCustomersByIds(accountHolders);
    }

    public List<CustomerDetailResponse> getCustomersByIds(List<Long> userIds) {
        return  customerRepository.findAllById(userIds).stream().map(customer -> CustomerDetailResponse.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .middleName(customer.getMiddleName())
                .profileImage(customer.getProfilePath())
                .customerId(customer.getId().toString())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .build()).toList();
    }
}
