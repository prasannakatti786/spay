package com.spay.wallet.accountservice.controller.services;


import com.spay.wallet.accountservice.controller.repo.AccountRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class AccountCreationService {
    private final AccountRepository accountRepository;
    private final LangUtil langUtil;
    private final PasswordEncoder passwordEncoder;
    private final CurrencyService currencyService;
    private final BusinessIdGenerator businessIdGenerator;
    private final WalletIdGenerator walletIdGenerator;

    @KafkaListener(topics = CustomTopic.DEFAULT_ACCOUNT_CREATION, groupId = "default-account-creation")
    public void createDefaultAccount(@Payload CreateDefaultAccountPayload payload) {
        validateDefaultAccountPayload(payload);
        verifyMsisdnExists(payload.getMsisdn());
        var currency = currencyService.getDefaultCurrencyCode();
        var accountType = payload.getUserType().equals("CUSTOMER") ? AccountType.WALLET_ACCOUNT : AccountType.AGENT_ACCOUNT;
        var account = getAccountObject(currency, payload.getAccountName(), payload.getAPin(), payload.getUserId(), accountType, payload.getMsisdn());
        var accountId = accountType.equals(AccountType.AGENT_ACCOUNT) ? businessIdGenerator.generateId() : walletIdGenerator.generateId();
        account.setAccountId(accountId);
        accountRepository.save(account);
    }

    public AccountCreationResponse createNewAccount(CreateAccountRequest accountRequest) {
        ValidateUtil.verifyUserId(accountRequest.getUserId(), langUtil);
        verifyMsisdnExists(accountRequest.getMsisdn());
        var currency = currencyService.getCurrencyCodeByCode(accountRequest.getCurrencyCode());
        var account = getAccountObject(currency, accountRequest.getAccountName(), accountRequest.getAPin(), accountRequest.getUserId(), accountRequest.getAccountType(), accountRequest.getMsisdn());
        var accountId = accountRequest.getAccountType().equals(AccountType.WALLET_ACCOUNT) ? walletIdGenerator.generateId() : businessIdGenerator.generateId();
        account.setAccountId(accountId);
        accountRepository.save(account);
        return new AccountCreationResponse(
                langUtil.message("account.service.info.created-successfully"),
                accountId, accountRequest.getUserId(), account.getAccountName());
    }

//    public CommonResponse changeAccountNameRequest(String accountId, String userId, String newAccountName) {
//        ValidateUtil.verifyUserId(userId, langUtil);
//        var account = getAccountById(accountId);
//        ValidateUtil.verifyIsAuthorAndAdmin(userId, account.getAccountHolders(), langUtil);
//        accountRepository.updateSpareAccountNameByAccountId(newAccountName, accountId);
//        return new CommonResponse(langUtil.message("account.service.info.account-name-change-please-wait"));
//    }
//
//    public CommonResponse acceptAccountNameRequest(String accountId) {
//        var account = getAccountById(accountId);
//        if (StringUtils.isEmpty(account.getSpareAccountName()))
//            throw new ApiException(langUtil.message("account.service.error.no-available-account-name-change-request"), HttpStatus.BAD_REQUEST);
//        accountRepository.updateSpareAccountNameAndAccountNameByAccountId("", account.getSpareAccountName(), accountId);
//        // TODO notify the customer
//        return new CommonResponse(langUtil.message("account.service.info.account-name-change-is-accepted"));
//    }
//
//    public CommonResponse rejectAccountNameRequest(String accountId) {
//        var account = getAccountById(accountId);
//        if (StringUtils.isEmpty(account.getSpareAccountName()))
//            throw new ApiException(langUtil.message("account.service.error.no-available-account-name-change-request"), HttpStatus.BAD_REQUEST);
//        accountRepository.updateSpareAccountNameAndAccountNameByAccountId("", account.getAccountName(), accountId);
//        // TODO notify the customer
//        return new CommonResponse(langUtil.message("account.service.info.account-name-change-is-rejected"));
//    }
//
//    public CommonResponse cancelAccountNameRequest(String accountId, String userId) {
//        ValidateUtil.verifyUserId(userId, langUtil);
//        var account = getAccountById(accountId);
//        ValidateUtil.verifyIsAuthorAndAdmin(userId, account.getAccountHolders(), langUtil);
//        if (StringUtils.isEmpty(account.getSpareAccountName()))
//            throw new ApiException(langUtil.message("account.service.error.no-available-account-name-change-request"), HttpStatus.BAD_REQUEST);
//        accountRepository.updateSpareAccountNameAndAccountNameByAccountId("", account.getSpareAccountName(), accountId);
//        return new CommonResponse(langUtil.message("account.service.info.account-name-change-is-rejected"));
//    }
//
//    private Account getAccountById(String accountId) {
//        return accountRepository.findById(accountId).orElseThrow(() -> new ApiException(langUtil.message("account.service.error.account-not-found"), HttpStatus.NOT_FOUND));
//    }
//
//    public CommonResponse updateSpareMsisdn(String userId, String accountId, String spareMsisdn) {
//        ValidateUtil.verifyUserId(userId, langUtil);
//        var account = getAccountById(accountId);
//        ValidateUtil.verifyIsAuthorAndAdmin(userId, account.getAccountHolders(), langUtil);
//        if (StringUtils.isEmpty(account.getSpareMsisdn()))
//            throw new ApiException(langUtil.message("account.service.error.spare-msisdn-not-exist"), HttpStatus.BAD_REQUEST);
//        return generateOtpAndUpdateSpareMsisdn(accountId, spareMsisdn, account);
//    }
//
//    public CommonResponse updateMsisdn(String userId, String accountId, String newMsisdn) {
//        ValidateUtil.verifyUserId(userId, langUtil);
//        var account = getAccountById(accountId);
//        ValidateUtil.verifyIsAuthorAndAdmin(userId, account.getAccountHolders(), langUtil);
//        if (StringUtils.isEmpty(account.getMsisdn()))
//            throw new ApiException(langUtil.message("account.service.error.msisdn-not-exist"), HttpStatus.BAD_REQUEST);
//        return generateOtpAndUpdateSpareMsisdn(accountId, newMsisdn, account);
//    }
//
//    public CommonResponse verifyOtpMsisdnChanges(String userId, String accountId,String otp){
//        ValidateUtil.verifyUserId(userId, langUtil);
//        var account = getAccountById(accountId);
//        ValidateUtil.verifyIsAuthorAndAdmin(userId, account.getAccountHolders(), langUtil);
//        verifyOtp(otp,account);
//        accountRepository.updateMsisdnAndSpareMsisdnByAccountId(account.getSpareMsisdn(),"",account.getAccountId());
//        accountRepository.updatePhoneOtpAndPhoneOtpExpiresAtByAccountId(null, LocalDateTime.now(Clock.systemUTC()),account.getAccountId());
//        return new CommonResponse(langUtil.message("account.service.info.created-successfully"));
//    }
//
//    private CommonResponse generateOtpAndUpdateSpareMsisdn(String accountId, String spareMsisdn, Account account) {
//        account.setSpareMsisdn(spareMsisdn.replaceAll(" ", ""));
//        var newOtp = RandomGenerator.generateNumber(6);
//        var newOtpExpiresAt = LocalDateTime.now(Clock.systemUTC());
//        // TODO send otp
//        accountRepository.updateSpareMsisdnAndPhoneOtpAndPhoneOtpExpiresAtByAccountId(spareMsisdn.replaceAll(" ", ""), newOtp, newOtpExpiresAt, accountId);
//        return new CommonResponse(langUtil.message("account.service.info.spare-msisdn-is-changed", spareMsisdn));
//    }
//
//    private Account getAccountObject(Currency currency, String accountName, String aPin, String userId, AccountType accountType, String msisdn) {
//        var accountBalance = AccountBalance.builder()
//                .balance(BigDecimal.ZERO).shadowBalance(BigDecimal.ZERO)
//                .pendingBalance(BigDecimal.ZERO).currencyCode(currency.getCurrencyCode()).currencyName(currency.getCurrencyName())
//                .currencySymbol(currency.getCurrencySymbol()).currencyId(currency.getCurrencyId().toString())
//                .currencyNumericCode(currency.getCurrencyNumericCode()).currencyType(currency.getCurrencyType()).build();
//        var accountLimit = AccountLimit.builder()
//                .accountBalanceLimit(currency.getDefaultAccountBalanceLimit())
//                .debitLimitPerTransaction(currency.getDefaultDebitLimitPerTransaction())
//                .debitLimitPerDay(currency.getDefaultDebitLimitPerTransaction())
//                .creditLimitPerDay(currency.getCreditLimitPerDay())
//                .creditedAmountToDay(BigDecimal.ZERO)
//                .lastCreditAt(LocalDateTime.now(Clock.systemUTC()))
//                .lastDebitAt(LocalDateTime.now(Clock.systemUTC()))
//                .debitedAmountToDay(BigDecimal.ZERO)
//                .build();
//        return Account.builder()
//                .phoneOtpExpiresAt(LocalDateTime.now(Clock.systemUTC()))
//                .accountBalance(accountBalance).accountLimit(accountLimit).aPin(passwordEncoder.encode(StringUtils.isNumeric(aPin) ? aPin : "0000"))
//                .accountHolders(List.of(AccountHolder.builder().accountHolderId(userId).isAdmin(Boolean.TRUE).build()))
//                .createdAt(LocalDateTime.now(Clock.systemUTC())).expireAt(LocalDateTime.now(Clock.systemUTC()).plusYears(3))
//                .modifiedAt(LocalDateTime.now(Clock.systemUTC())).accountStatus(AccountStatus.ACTIVE).accountType(accountType)
//                .spareMsisdn("").msisdn(msisdn.replaceAll(" ", "")).accountName(StringUtils.capitalize(accountName)).iban("").build();
//    }
//
//    private void verifyMsisdnExists(String msisdn) {
//        var isMsisdnExists = accountRepository.existsByMsisdn(msisdn.replaceAll(" ", ""));
//        var isSpareMsisdnExists = accountRepository.existsBySpareMsisdn(msisdn.replaceAll(" ", ""));
//        if (isMsisdnExists)
//            throw new ApiException(langUtil.message("account.service.error.msisdn-already-exist"), HttpStatus.BAD_REQUEST);
//        if (isSpareMsisdnExists)
//            throw new ApiException(langUtil.message("account.service.error.msisdn-already-exist"), HttpStatus.BAD_REQUEST);
//    }
//
//    private void verifyOtp(String otp, Account account) {
//        if (account.getPhoneOtp() == null)
//            throw new ApiException(langUtil.message("account.service.error.no-otp-sent"), HttpStatus.BAD_REQUEST);
//        if (!account.getPhoneOtp().equals(otp))
//            throw new ApiException(langUtil.message("account.service.error.otp-invalid"), HttpStatus.BAD_REQUEST);
//        if (account.getPhoneOtpExpiresAt().isBefore(LocalDateTime.now(Clock.systemUTC()))) {
//            var otpExpiresAt = LocalDateTime.now(Clock.systemUTC()).plusMinutes(3);
//            var newOtp = RandomGenerator.generateNumber(6);
//            accountRepository.updatePhoneOtpAndPhoneOtpExpiresAtByAccountId(newOtp,otpExpiresAt,account.getAccountId());
//            //TODO send otp again
//            throw new ApiException(langUtil.message("account.service.error.otp-expired"), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    private void validateDefaultAccountPayload(CreateDefaultAccountPayload accountPayload) {
//        if (accountPayload.getAccountName() == null)
//            throw new IllegalStateException("Account name must be provided");
//        if (accountPayload.getMsisdn() == null)
//            throw new IllegalStateException("Msisdn must be provided");
//        if (accountPayload.getUserType() == null)
//            throw new IllegalStateException("User type must be provided");
//
//    }

}
