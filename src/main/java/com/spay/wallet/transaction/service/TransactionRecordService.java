package com.spay.wallet.transaction.service;

import com.spay.wallet.account.entities.AccountType;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.services.AccountBalanceService;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.notifications.service.TransactionRecordNotificationService;
import com.spay.wallet.transaction.entities.TransactionStatus;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.properies.WalletProperties;
import com.spay.wallet.transaction.dto.TransactionRecord;
import com.spay.wallet.transaction.reqRes.*;
import com.spay.wallet.transaction.entities.Transaction;
import com.spay.wallet.transaction.repo.TransactionRepository;
import com.spay.wallet.common.CustomPage;
import com.spay.wallet.common.WalletFormat;
import com.spay.wallet.transaction.utilities.TransactionIdGenerator;
import com.spay.wallet.transaction.utilities.TransactionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionRecordService {
    private final TransactionRepository transactionRepository;
    private final TransactionRecordNotificationService recordNotificationService;
    private final TransactionIdGenerator transactionIdGenerator;
    private final WalletProperties walletProperties;
    private final AccountBalanceService accountBalanceService;


    public TransactionHistoryResponse saveTransactionRecord(TransactionRecord record){
        var transaction =  new Transaction(record);
        var transactionId = transactionIdGenerator.generateId();
        transaction.setTransactionId(transactionId);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.saveAndFlush(transaction);
        if(!record.getTransactionType().equals(TransactionType.RECHARGE_SETTLEMENT))
            notifyCustomer(record, transaction, transactionId);
        return TransactionUtil.mapTransactionToTransactionHistoryResponse(transaction, record.getSenderAccount(), walletProperties);
    }

    private void notifyCustomer(TransactionRecord record, Transaction transaction, String transactionId) {
        var senderMessage  = TransactionUtil.getSenderMessage(record, transaction, transactionId, record.getPaymentChannel(), walletProperties);
        var receiverMessage  = TransactionUtil.getReceiverMessage(record, transaction, transactionId, record.getPaymentChannel(), walletProperties);
        var transactionResponse =  new TransactionRecordResponse(transactionId,senderMessage,receiverMessage);
        recordNotificationService.notifyCustomer(transactionResponse, record.getSenderAccount(),
                record.getReceiverAccount(), record.getTransactionType(), record.getPaymentChannel());
    }



    public List<TransactionHistoryResponse> miniStatement(String accountId, String customerId){
      var account =   accountBalanceService.getAccount(accountId,Long.parseLong(customerId));
      var pageRequest = createPage("transactionType", Sort.Direction.DESC,0,10);
      var transactions = transactionRepository.findBySenderAccountOrReceiverAccountOrderByTransactionDateDesc(account.getAccountId(),account.getAccountId(),pageRequest);
      return transactions.stream().map(transaction -> TransactionUtil.mapTransactionToTransactionHistoryResponse(transaction,account.getAccountId(), walletProperties)).toList();
    }

    public  List<TransactionHistoryResponse> getPendingTransactions(String accountId, String customerId){
        var account =   accountBalanceService.getAccount(accountId,Long.parseLong(customerId));
        if(!account.getAccountType().equals(AccountType.AGENT_ACCOUNT))
            throw new ApiException("Agents account can see withdrawal request",HttpStatus.NOT_ACCEPTABLE);
        var transactions = transactionRepository.findByReceiverAccountAndTransactionStatusAndTransactionType(accountId,TransactionStatus.PENDING,TransactionType.WITHDRAWAL);
        return transactions.stream().map(transaction -> TransactionUtil.mapTransactionToTransactionHistoryResponse(transaction,account.getAccountId(), walletProperties)).toList();
    }

    public  List<TransactionHistoryResponse> getTransactionByDate(String accountId, String customerId, LocalDate startDate, LocalDate endDate){
        var account =   accountBalanceService.getAccount(accountId,Long.parseLong(customerId));
        var start = startDate.atStartOfDay();
        var end =   endDate.atTime(LocalTime.of(23,59,59));
        var transactions =  transactionRepository.findBySenderAccountAndReceiverAccountAndTransactionDateAfterAndTransactionDateBeforeOrderByTransactionDate(accountId,accountId,start,end);
        return transactions.stream().map(transaction -> TransactionUtil.mapTransactionToTransactionHistoryResponse(transaction,account.getAccountId(), walletProperties)).toList();
    }

    public  void getTransactionByDateAndSendEmail(String accountId, String customerId, LocalDate startDate, LocalDate endDate){
        var transactions =  getTransactionByDate(accountId,customerId,startDate,endDate);
        // TODO Create transaction template for email and send.
    }

    public  List<TransactionHistoryResponse> getTransactionByDateAndAccount(String accountId, String customerId,String otherAccount,  LocalDate startDate, LocalDate endDate){
        var account =   accountBalanceService.getAccount(accountId,Long.parseLong(customerId));
        otherAccount =  getReceiverAccount(otherAccount);
        accountBalanceService.checkIsSameAccount(accountId,otherAccount);
        accountBalanceService.isAccountExist(otherAccount);
        var start = startDate.atStartOfDay();
        var end =   endDate.atTime(LocalTime.of(23,59,59));
        var transactions = transactionRepository.findDistinctBySenderAccountOrReceiverAccountOrReceiverAccountOrSenderAccountAndTransactionDateBetween(accountId,otherAccount,start,end);
        return transactions.stream().map(transaction -> TransactionUtil.mapTransactionToTransactionHistoryResponse(transaction,account.getAccountId(),walletProperties)).toList();
    }

    private  String getReceiverAccount(String receiverAccount) {
        receiverAccount =  StringUtils.trimAllWhitespace(receiverAccount);
        if(!receiverAccount.contains(walletProperties.getAccountIdSuffix()))
            return receiverAccount+walletProperties.getAccountIdSuffix();
        return receiverAccount;
    }






    private PageRequest createPage(String sortByProperty, Sort.Direction direction, int pageNumber,int pageSize){
        return PageRequest.of(pageNumber, pageSize, direction, sortByProperty);
    }

    public TransactionTypeCountResponse countTransaction(){
       var counts =  transactionRepository.countTransactionByCategory();
      return new  TransactionTypeCountResponse(counts);
    }

    public ProfitByCategoryResponse getProfitsByCategory(LocalDate start, LocalDate end,CurrencyCode currencyCode){
      var profits =  transactionRepository.sumProfitsByCategory(start.atStartOfDay(),end.atTime(23,59,59),currencyCode);
      return new ProfitByCategoryResponse(profits,currencyCode);
    }

    public TransactionAmountsCategoryResponse transactionAmountsCategoryResponse(LocalDateTime start, LocalDateTime end,CurrencyCode currencyCode, String type){
        if(type.equals("day"))
           return transactionAmountsCategoryResponseByDay(start,end,currencyCode);
        if(type.equals("month"))
            return  transactionAmountsCategoryResponseByMonth(start,end,currencyCode);
        return null;
    }

    private TransactionAmountsCategoryResponse transactionAmountsCategoryResponseByDay(LocalDateTime start, LocalDateTime end,CurrencyCode currencyCode){
       var hours = ChronoUnit.HOURS.between(start,end);
        if(hours > 24)
            throw new ApiException("Hours between starting date and ending date must be 24h or less then 24h now its "+hours+"h", HttpStatus.BAD_REQUEST);
        var transactions =  transactionRepository.transactionsByHour(start,end, currencyCode);
        return new TransactionAmountsCategoryResponse(transactions,currencyCode);
    }

    private TransactionAmountsCategoryResponse transactionAmountsCategoryResponseByMonth(LocalDateTime start, LocalDateTime end,CurrencyCode currencyCode){
        var dataStart = start.getYear()+""+start.getMonth().getValue();
        var dataEnd = end.getYear()+""+end.getMonth().getValue();
        if(!dataStart.equals(dataEnd))
            throw new ApiException("Days between starting date and ending date must be 1 month or less then 1 month", HttpStatus.BAD_REQUEST);

        var transactions =  transactionRepository.transactionsByDay(start,end, currencyCode);
        return new TransactionAmountsCategoryResponse(transactions,currencyCode);
    }


    public CustomPage<Transaction> getTransactions(Integer page, Integer size, TransactionType type){
        var pageRequest = createPage("transactionDate", Sort.Direction.DESC,page,size);
        var transactionsPage = type == null? transactionRepository.findAll(pageRequest): transactionRepository.findAllByTransactionType(type,pageRequest);
        return new CustomPage<>(transactionsPage);
    }

    public CustomPage<Transaction> getSettlementTransactions(Integer page, Integer size){
        var pageRequest = createPage("transactionDate", Sort.Direction.DESC,page,size);
        var transactionsPage = transactionRepository.findAllByTransactionType(TransactionType.RECHARGE_SETTLEMENT,pageRequest);
        return new CustomPage<>(transactionsPage);
    }

    public Transaction getTransaction(Long transactionId){
       return transactionRepository.findById(transactionId).orElseThrow(() -> new ApiException("Transaction id of"+transactionId+" is not found",HttpStatus.NOT_FOUND));
    }


    public CustomPage<Transaction> getAccountTransactions(Integer page, Integer size, TransactionType type,String accountNo){
        var pageRequest = createPage("transactionDate", Sort.Direction.DESC,page,size);
        var transactionsPage = type == null? transactionRepository.findAllByAccount(accountNo,pageRequest): transactionRepository.findAllByTransactionTypeAndAccount(type,accountNo,pageRequest);
        return new CustomPage<>(transactionsPage);
    }



    public List<Transaction> searchTransaction(String key) {
       return  transactionRepository.findByTransactionIdLikeIgnoreCaseOrSenderAccountLikeIgnoreCaseOrReceiverAccountLikeIgnoreCase(key);
    }

    public void updateTransactionStatus(Long transactionId, TransactionStatus transactionStatus) {
        transactionRepository.updateTransactionStatusByTransactionId(transactionStatus,transactionId.toString());
    }



}
