package com.spay.wallet.account.services;


import com.spay.wallet.account.entities.*;
import com.spay.wallet.account.repo.AccountRepository;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountCreationRequest;
import com.spay.wallet.account.reqAndRes.accountCreation.AccountCreationResponse;
import com.spay.wallet.account.utilities.AccountIdGenerator;
import com.spay.wallet.account.utilities.currency.CurrencySelection;
import com.spay.wallet.customer.entities.CustomerLevel;
import com.spay.wallet.customer.repo.CustomerRepository;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCreationService {
    private final AccountRepository accountRepository;
    private final AccountIdGenerator accountIdGenerator;
    private final CustomerRepository customerRepository;
    private final Map<String,CurrencySelection> currencySelection;
    private final PasswordEncoder passwordEncoder;

    public void createMainAccount(String customerId, String customerName){
        if(checkMainAccountByHolder(customerId)) return;
        var currency = Currency.getInstance("USD");
        var accountId = accountIdGenerator.generateId();
        var mainAccount = Account.builder()
                .accountName(customerName.trim())
                .accountHolders(List.of(AccountHolder.builder()
                                .isAdmin(Boolean.TRUE)
                                .accountHolderId(customerId).build()))
                .accountId(accountId).accountStatus(AccountStatus.ACTIVE)
                .accountType(AccountType.MAIN_ACCOUNT).currencyType(CurrencyType.FIAT)
                .currencyCode(CurrencyCode.USD).currencyName(currency.getDisplayName())
                .currencySymbol(currency.getSymbol()).currencyNumericCode(currency.getNumericCode())
                .createdAt(LocalDateTime.now())
                .aPin(passwordEncoder.encode("0000"))
                .balance(BigDecimal.ZERO).shadowBalance(BigDecimal.ZERO).pendingBalance(BigDecimal.ZERO)
                .expireAt(LocalDateTime.now().plusMonths(3))
                .modifiedAt(LocalDateTime.now())
                .build();
        accountRepository.save(mainAccount);
    }

    public void createAgentAccount(String customerId, String agentName){
        var isExist =  accountRepository.existsByAccountTypeAndAccountHolders_AccountHolderId(AccountType.AGENT_ACCOUNT,customerId);
        if(isExist) return;
        var currency = Currency.getInstance("USD");
        var accountId = accountIdGenerator.generateId();
        var mainAccount = Account.builder()
                .accountName(agentName.trim())
                .accountHolders(List.of(AccountHolder.builder()
                        .isAdmin(Boolean.TRUE)
                        .accountHolderId(customerId).build()))
                .accountId(accountId).accountStatus(AccountStatus.ACTIVE)
                .accountType(AccountType.AGENT_ACCOUNT).currencyType(CurrencyType.FIAT)
                .currencyCode(CurrencyCode.USD).currencyName(currency.getDisplayName())
                .currencySymbol(currency.getSymbol()).currencyNumericCode(currency.getNumericCode())
                .createdAt(LocalDateTime.now())
                .aPin(passwordEncoder.encode("0000"))
                .balance(BigDecimal.ZERO).shadowBalance(BigDecimal.ZERO).pendingBalance(BigDecimal.ZERO)
                .expireAt(LocalDateTime.now().plusMonths(3))
                .modifiedAt(LocalDateTime.now())
                .build();
        accountRepository.save(mainAccount);
    }

    public void createSettlementAccount(){
        var isExist = accountRepository.existsByAccountType(AccountType.SETTLEMENT_ACCOUNT);
        if(isExist){
            log.warn("Settlement account is already exist");
            return;
        }
        var currency = Currency.getInstance("USD");
        var accountId = accountIdGenerator.generateId();
        var mainAccount = Account.builder()
                .accountName("Settlement Account")
                .accountHolders(List.of(AccountHolder.builder()
                        .isAdmin(Boolean.TRUE)
                        .accountHolderId("admin-settlement-account").build()))
                .accountId(accountId).accountStatus(AccountStatus.ACTIVE)
                .accountType(AccountType.SETTLEMENT_ACCOUNT).currencyType(CurrencyType.FIAT)
                .currencyCode(CurrencyCode.USD).currencyName(currency.getDisplayName())
                .currencySymbol(currency.getSymbol()).currencyNumericCode(currency.getNumericCode())
                .createdAt(LocalDateTime.now())
                .aPin(passwordEncoder.encode("0000"))
                .balance(BigDecimal.ZERO).shadowBalance(BigDecimal.ZERO).pendingBalance(BigDecimal.ZERO)
                .expireAt(LocalDateTime.now().plusMonths(3))
                .modifiedAt(LocalDateTime.now())
                .build();
        accountRepository.save(mainAccount);
        log.info("Settlement account is created successfully {}",accountId);
    }



    public void changeAPin(String customerId, String accountId, String aPin,String oldAPin){
        var accountOptional = accountRepository.findByAccountIdAndAccountHolders_AccountHolderIdAndAccountHolders_IsAdminTrue(accountId, customerId);
        if(accountOptional.isEmpty())
            throw new ApiException("Account does not exist or you are not authorized",HttpStatus.NOT_FOUND);

        var account = accountOptional.get();
        if(!passwordEncoder.matches(oldAPin,account.getAPin()))
            throw new ApiException("Old A-PIN is not correct",HttpStatus.NOT_ACCEPTABLE);
        if(!StringUtils.isNumeric(aPin))
            throw new ApiException("A-PIN must be digits",HttpStatus.NOT_ACCEPTABLE);
        if(oldAPin.equals(aPin))
            throw new ApiException("New A-PIN is same like old A-PIN",HttpStatus.NOT_ACCEPTABLE);
        if(aPin.length() != 4)
            throw new ApiException("A-PIN must be contained 4 digits",HttpStatus.NOT_ACCEPTABLE);

        account.setAPin(passwordEncoder.encode(aPin));
        accountRepository.save(account);
    }

    public void forgetAPin(String customerId, String accountId, String password, String newAPIN){
        var accountOptional = accountRepository.findByAccountIdAndAccountHolders_AccountHolderIdAndAccountHolders_IsAdminTrue(accountId, customerId);
        var customerOptional =  customerRepository.findById(Long.parseLong(customerId));
        if(customerOptional.isEmpty())
            throw new ApiException("Customer does not exist", HttpStatus.NOT_FOUND);
        if(accountOptional.isEmpty())
            throw new ApiException("Account does not exist or you are not authorized",HttpStatus.NOT_FOUND);
        var customer = customerOptional.get();
        var account = accountOptional.get();
        var credential =  customer.getCredential();
        if(!passwordEncoder.matches(password,credential.getPassword()))
            throw new ApiException("Invalid password",HttpStatus.NOT_ACCEPTABLE);
        account.setAPin(passwordEncoder.encode(newAPIN));
        accountRepository.save(account);
    }


    public AccountCreationResponse createNewAccountByCustomer(Long customerId,AccountCreationRequest request){
        var accountType =  request.getAccountType();
        var accountName = request.getAccountName();
        var currencyCode = request.getCurrencyCode();
        var aPin = request.getAPin();

        if(aPin.length() != 4)
            throw new ApiException("A-PIN must be contained 4 digits",HttpStatus.NOT_ACCEPTABLE);
        if(!StringUtils.isNumeric(aPin))
          throw new ApiException("A-PIN must be digits",HttpStatus.NOT_ACCEPTABLE);
//
//        if(!customerRepository.existsByIdAndCustomerLevel(customerId, CustomerLevel.FULL_CUSTOMER))
//            throw new ApiException("Customer is not qualified customer.", HttpStatus.NOT_FOUND);
        var notAllowedAccountTypes = List.of(AccountType.SETTLEMENT_ACCOUNT,AccountType.AGENT_ACCOUNT);
        if (notAllowedAccountTypes.contains(accountType))
            throw new ApiException("Account type is not allowed for customers",HttpStatus.NOT_ACCEPTABLE);
        var isExist = accountRepository.existsByAccountHolders_AccountHolderIdAndAccountStatusInAndAccountType(customerId.toString(),List.of(AccountStatus.ACTIVE,AccountStatus.LOST),accountType);
        if(isExist)
            throw new ApiException("You already have this account type",HttpStatus.NOT_ACCEPTABLE);
        var newAccountId =  createAnAccount(customerId,currencyCode,accountName,accountType,aPin);
        return new AccountCreationResponse(
                "New %s has been created in %s %s and account name is %s".formatted(accountType.name(),
                        currencyCode.getCurrencyType().name(),currencyCode.name(),accountName),
                newAccountId, customerId.toString(), accountName
        );
    }

    public AccountCreationResponse addNewAccountHolderToJoinAccount(Long customerId,String accountId,Long joinCustomerId){
        if(!customerRepository.existsById(joinCustomerId))
            throw new ApiException("Customer does not exist", HttpStatus.NOT_FOUND);
        if(!customerRepository.existsById(customerId))
            throw new ApiException("Joiner customer does not exist", HttpStatus.NOT_FOUND);
        var accountOptional = accountRepository.findByAccountIdAndAccountHolders_AccountHolderIdAndAccountHolders_IsAdminTrue(accountId,customerId.toString());
        if(accountOptional.isEmpty())
            throw new ApiException("Account does not exist or you are not authorized",HttpStatus.NOT_FOUND);
        var account = accountOptional.get();
        if(!account.getAccountStatus().equals(AccountStatus.ACTIVE))
            throw new ApiException("Account is not active",HttpStatus.NOT_ACCEPTABLE);
        if(account.getAccountHolders().stream().anyMatch(accountHolder -> accountHolder.getAccountHolderId().equals(joinCustomerId.toString())))
            throw new ApiException("This customer is already joined to this account",HttpStatus.NOT_ACCEPTABLE);
        if(!List.of(AccountType.JOIN_ACCOUNT,AccountType.BUSINESS_ACCOUNT).contains(account.getAccountType()))
            throw new ApiException("Cannot join this account please create a join account or business account",HttpStatus.NOT_ACCEPTABLE);
        var newAccountHolder =  new AccountHolder(null,joinCustomerId.toString(),Boolean.FALSE);
        var existingHolders = account.getAccountHolders();
        existingHolders.add(newAccountHolder);
        account.setAccountHolders(existingHolders);
        accountRepository.save(account);
        return  new AccountCreationResponse(
                "you successfully joined this "+joinCustomerId+" account name "+account.getAccountName(),
                account.getAccountId(),
                existingHolders.stream().map(AccountHolder::getAccountHolderId).reduce((accountHolder, accountHolder2) -> accountHolder+", "+accountHolder2).orElse("----"),
                account.getAccountName()
        );
    }


    private String createAnAccount(Long customerId, CurrencyCode code, String accountName,AccountType accountType,String aPin){

        var accountId =  accountIdGenerator.generateId();
        var currency =  currencySelection.get(code.getCurrencyType().name()).getCurrencyInfo(code);
        var account = Account.builder()
                .accountName(StringUtils.capitalize(accountName).trim())
                .accountHolders(List.of(AccountHolder.builder()
                                .isAdmin(Boolean.TRUE)
                        .accountHolderId(customerId.toString()).build())).accountId(accountId).accountStatus(AccountStatus.ACTIVE)
                .accountType(accountType).currencyType(currency.getType())
                .currencyCode(currency.getCode()).currencyName(currency.getName())
                .currencySymbol(currency.getSymbol()).currencyNumericCode(currency.getNumericCode())
                .createdAt(LocalDateTime.now())
                .aPin(passwordEncoder.encode(aPin))
                .balance(BigDecimal.ZERO).shadowBalance(BigDecimal.ZERO).pendingBalance(BigDecimal.ZERO)
                .expireAt(LocalDateTime.now().plusMonths(3))
                .modifiedAt(LocalDateTime.now())
                .build();

        accountRepository.save(account);
        return accountId;
    }



    public Boolean checkMainAccountByHolder(String customerId){
        return accountRepository.existsByAccountTypeAndAccountHolders_AccountHolderId(AccountType.MAIN_ACCOUNT,customerId);
    }


}
