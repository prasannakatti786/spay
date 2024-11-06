package com.spay.wallet.customer.utilities;

import com.spay.wallet.customer.entities.CustomerIdSequence;
import com.spay.wallet.customer.repo.CustomerIdSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CustomerIdGenerator {

    private final CustomerIdSequenceRepository repository;

    public Long generateId(){
        var date = LocalDate.now();
        var  year = String.valueOf(date.getYear()).substring(2);
        var  month =String.format("%02d",date.getMonthValue());
        var  day = String.valueOf(date.getDayOfMonth());
        var id =  getSequence();
        return Long.parseLong(year+month+day+id);
    }

    private String getSequence(){
        var sequence =  repository.findAll().stream().findAny().orElse(new CustomerIdSequence(null, 0L));
        if(sequence.getCurrentId() == 9999)
            sequence.setCurrentId(0L);
        sequence.setCurrentId(sequence.getCurrentId()+1);
        repository.save(sequence);
        return String.format("%04d", sequence.getCurrentId());
    }


}
