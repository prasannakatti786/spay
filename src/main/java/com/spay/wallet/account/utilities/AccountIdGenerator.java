package com.spay.wallet.account.utilities;

import com.spay.wallet.account.entities.AccountIdSequence;
import com.spay.wallet.account.repo.AccountIdSequenceRepository;
import com.spay.wallet.properies.WalletProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class AccountIdGenerator {
    private final IdDate idDate;
    private final AccountIdSequenceRepository repository;
    private final WalletProperties walletProperties;

    public String generateId(){
        var date = idDate.getCurrentDate();
        var  year = new StringBuilder(String.valueOf(date.getYear()).substring(2)).reverse().toString() ;
        var  month = new StringBuilder(String.format("%02d",date.getMonthValue())).reverse().toString();
        var id =  getSequence();
        var accountId =  String.format("%s%s%s%s",year,month,id,walletProperties.getAccountIdSuffix());
        updateLastId(accountId);
        return accountId;
    }

    private void updateLastId(String id) {
        var sequence =  repository.findAll().stream().findAny().orElse(new AccountIdSequence(null, 0L,null));
        sequence.setLastId(id);
        repository.save(sequence);
    }

    private String getSequence(){
        var sequence =  repository.findAll().stream().findAny().orElse(new AccountIdSequence(null, 0L,null));
        if(!isSameDate(sequence.getLastId()))
            sequence.setCurrentId(0L);
        sequence.setCurrentId(sequence.getCurrentId()+1);
        repository.save(sequence);
        return String.format("%04d", sequence.getCurrentId());
    }

    private Boolean isSameDate(String lastId){
        if(lastId == null)
            return false;
        var date = idDate.getCurrentDate();
        var  year = new StringBuilder(String.valueOf(date.getYear()).substring(2)).reverse().toString() ;
        var  month = new StringBuilder(String.format("%02d",date.getMonthValue())).reverse().toString();
        return  lastId.substring(0,4).equals(year+month);
    }


}
