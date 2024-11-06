package com.spay.wallet.account.services;

import com.spay.wallet.account.entities.AccountHolder;
import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.account.entities.QuickPayAccount;
import com.spay.wallet.account.repo.QuickPayAccountRepository;
import com.spay.wallet.account.reqAndRes.quickPay.QuickPayRequest;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuickPayAccountService {

        private final QuickPayAccountRepository quickPayAccountRepository;
        private final AccountService accountService;

        public void addAccount(QuickPayRequest account, String customerId){
                var newAccount = StringUtils.trimAllWhitespace(account.getAccount());
                var isAlreadyExist = quickPayAccountRepository.existsByAccountAndCustomerId(newAccount,customerId);
                if(isAlreadyExist)
                        throw new ApiException("You already added", HttpStatus.BAD_REQUEST);
                var theAccount = accountService.isAccountExist(newAccount);
                var admin = theAccount.getAccountHolders().stream().filter(AccountHolder::getIsAdmin).map(AccountHolder::getAccountHolderId).findFirst().orElse("");
                var quickAccount = new QuickPayAccount(null,newAccount,customerId, theAccount.getAccountName(),admin,theAccount.getCurrencyCode(),LocalDateTime.now());
                quickPayAccountRepository.save(quickAccount);
        }

        public List<QuickPayAccount> getQuickPayAccounts(String customerId, CurrencyCode currencyCode, Integer page){
                var pageRequest = PageRequest.of(page, 50, Sort.Direction.DESC, "lastModify");
                return quickPayAccountRepository.findByCustomerIdAndCurrencyCode(customerId,currencyCode,pageRequest);
        }

        public void deleteQuickPayAccount(String customerId,String account){
                quickPayAccountRepository.deleteByCustomerIdAndAccount(customerId,account);
        }

        public void modifyLastUpdate(String account, String customerId){
                quickPayAccountRepository.updateLastModifyByAccountAndCustomerId(LocalDateTime.now(),account,customerId);
        }
}

