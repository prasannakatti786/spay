package com.spay.wallet.transaction.utilities;

import com.spay.wallet.transaction.entities.TransactionIdSequence;
import com.spay.wallet.transaction.repo.TransactionIdSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransactionIdGenerator {
    private final TransactionIdSequenceRepository repository;

    public String generateId(){
        var date = LocalDateTime.now();
        var  year = String.valueOf(date.getYear()).substring(2) ;
        var  month = String.format("%02d",date.getMonthValue());
        var  day = String.format("%02d",date.getDayOfMonth());
        var id =  getSequence();
            return String.format("%s%s%s%s",year,month,day,id);
    }

    private String getSequence(){
        var sequence =  repository.findAll().stream().findAny().orElse(new TransactionIdSequence(null, 0L));
        sequence.setCurrentId(sequence.getCurrentId()+1);
        repository.save(sequence);
        return String.format("%07d", sequence.getCurrentId());
    }



}
