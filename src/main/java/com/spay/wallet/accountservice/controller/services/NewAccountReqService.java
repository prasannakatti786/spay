package com.spay.wallet.accountservice.controller.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class NewAccountReqService {
    private final NewAccountReqRepository newAccountReqRepository;
    private final LangUtil langUtil;
    private final CurrencyService currencyService;
    private final AccountCreationService accountCreationService;

    public List<NewAccountReqResponse> getNewAccountReqs(String userId){
        return newAccountReqRepository.findByUserId(userId).stream().map(account->NewAccountReqResponse.builder()
                .id(account.getId()).accountName(account.getSpareAccountName()).accountType(account.getAccountType())
                .msisdn(account.getSpareMsisdn()).currencyCode(account.getCurrencyCode()).build()).toList();
    }

    public CommonResponse createNewAccount(String userId, AccountType accountType, CreateAccountRequest accountRequest){
        if(!PhoneNumberUtils.isValidPhoneNumber(accountRequest.getMsisdn(),"ZZ"))
            throw new ApiException(langUtil.message("account.request.msisdn-invalid"), HttpStatus.BAD_REQUEST);
        var isValidExist = newAccountReqRepository.existsByUserIdAndSpareMsisdn(userId, accountRequest.getMsisdn().replaceAll(" ",""));
        if(isValidExist)
            throw new ApiException(langUtil.message("account.service.error.msisdn-already-exist"),HttpStatus.BAD_REQUEST);
        ValidateUtil.validateAPinIsNumeric(accountRequest.getAPin(),langUtil);
        var currency = currencyService.getCurrencyCodeByCode(accountRequest.getCurrencyCode().toUpperCase());
        var account = NewAccountReq.builder().userId(userId).spareMsisdn(accountRequest.getMsisdn().replaceAll(" ",""))
                .spareAccountName(StringUtils.capitalize(accountRequest.getAccountName())).accountType(accountType).aPin(accountRequest.getAPin())
                .currencyCode(currency.getCurrencyCode()).requestedAt(LocalDateTime.now(Clock.systemUTC())).build();
        var otp = RandomGenerator.generateNumber(6);
        account.setPhoneOtp(otp);
        account.setPhoneOtpExpiresAt(LocalDateTime.now(Clock.systemUTC()));
        newAccountReqRepository.save(account);
        return new CommonResponse( langUtil.message("account.service.info.verify-msisdn-otp", account.getSpareMsisdn()));
    }

//    public AccountCreationResponse verifySpareMsisdnOtp(Long accountId, String userId, String otp) {
//        ValidateUtil.verifyUserId(userId,langUtil);
//        var account = getNewAccountReq(userId, accountId);
//        verifyOtp(otp, account);
//        var request =  new CreateAccountRequest(account.getSpareAccountName(),account.getAccountType(),account.getSpareMsisdn(),account.getCurrencyCode(),account.getAPin(),account.getUserId());
//        var createdAccountResponse =  accountCreationService.createNewAccount(request);
//        deletedAllBySpareMsisdn(account.getSpareMsisdn());
//        return createdAccountResponse;
//    }
//
//    private void verifyOtp(String otp, NewAccountReq account) {
//        if (account.getPhoneOtp() == null)
//            throw new ApiException(langUtil.message("account.service.error.no-otp-sent"), HttpStatus.BAD_REQUEST);
//        if (!account.getPhoneOtp().equals(otp))
//            throw new ApiException(langUtil.message("account.service.error.otp-invalid"), HttpStatus.BAD_REQUEST);
//        if (account.getPhoneOtpExpiresAt().isBefore(LocalDateTime.now(Clock.systemUTC()))) {
//            var otpExpiresAt = LocalDateTime.now(Clock.systemUTC()).plusMinutes(3);
//            var newOtp = RandomGenerator.generateNumber(6);
//            newAccountReqRepository.updatePhoneOtpAndPhoneOtpExpiresAtById(newOtp,otpExpiresAt,account.getId());
//            //TODO send otp again
//            throw new ApiException(langUtil.message("account.service.error.otp-expired"), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    public CommonResponse resendOtp(String userId,Long id){
//        var account =  getNewAccountReq(userId,id);
//        var otp = RandomGenerator.generateNumber(6);
//        account.setPhoneOtp(otp);
//        account.setPhoneOtpExpiresAt(LocalDateTime.now(Clock.systemUTC()));
//        //TODO send otp again
//        return new CommonResponse( langUtil.message("account.service.info.verify-msisdn-otp"));
//    }
//
//    public CommonResponse updateRequestedAccount(CreateAccountRequest accountRequest,AccountType accountType,String userId,Long id){
//        var account = getNewAccountReq(userId, id);
//        var isOldAndNewSame =  account.getSpareMsisdn().equals(accountRequest.getMsisdn());
//        account.setAccountType(accountRequest.getAccountType());
//        account.setSpareAccountName(StringUtils.capitalize(accountRequest.getAccountName()));
//        ValidateUtil.validateAPinIsNumeric(accountRequest.getAPin(),langUtil);
//        account.setAPin(accountRequest.getAPin());
//        account.setAccountType(accountType == null? account.getAccountType():accountType);
//        var currency = currencyService.getCurrencyCodeByCode(accountRequest.getCurrencyCode().toUpperCase());
//        account.setCurrencyCode(currency.getCurrencyCode());
//        account.setSpareMsisdn(accountRequest.getMsisdn());
//        if(!isOldAndNewSame){
//            if(!PhoneNumberUtils.isValidPhoneNumber(accountRequest.getMsisdn(),"ZZ"))
//                throw new ApiException(langUtil.message("account.request.msisdn-invalid"), HttpStatus.BAD_REQUEST);
//            account.setSpareMsisdn(accountRequest.getMsisdn().replaceAll(" ",""));
//            var otp = RandomGenerator.generateNumber(6);
//            account.setPhoneOtp(otp);
//            account.setPhoneOtpExpiresAt(LocalDateTime.now(Clock.systemUTC()));
//        }
//        newAccountReqRepository.save(account);
//        if(isOldAndNewSame)
//            return new  CommonResponse(langUtil.message("account.service.info.account-updated-successfully"));
//
//        // TODO send otp
//        return new CommonResponse( langUtil.message("account.service.info.verify-msisdn-otp",account.getSpareMsisdn()));
//    }
//
//
//    private void deletedAllBySpareMsisdn(String msisdn){
//        newAccountReqRepository.deleteBySpareMsisdn(msisdn);
//    }
//
//    public CommonResponse deleteNewAccountRequest(String userId,Long id){
//        newAccountReqRepository.deleteByIdAndUserId(id,userId);
//        return new CommonResponse(langUtil.message("account.service.info.account-request-deleted-successfully"));
//    }
//
//    private NewAccountReq getNewAccountReq(String userId,Long id){
//        var rqAccount = newAccountReqRepository.findById(id).orElseThrow(()-> new ApiException(langUtil.message("account.service.error.account-not-found"), HttpStatus.NOT_FOUND));
//        if(!rqAccount.getUserId().equals(userId))
//            throw new ApiException(langUtil.message("account.service.error.not-author-account"), HttpStatus.NOT_ACCEPTABLE);
//        return rqAccount;
//    }


}
