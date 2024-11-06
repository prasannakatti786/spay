package com.spay.wallet.customerservice.utilities;
import com.spay.wallet.customerservice.entities.CustomerIdSequence1;
import com.spay.wallet.customerservice.repo.CustomerIdSequenceRepository1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CustomerIdGenerator1 {

    private final CustomerIdSequenceRepository1 repository;

    public Long generateId(){
        var date = LocalDate.now();
        var  year = String.valueOf(date.getYear()).substring(2);
        var  month =String.format("%02d",date.getMonthValue());
        var  day = String.valueOf(date.getDayOfMonth());
        var id =  getSequence();
        return Long.parseLong(year+month+day+id);
    }

    private String getSequence(){
        var sequence =  repository.findAll().stream().findAny().orElse(new CustomerIdSequence1(null, 0L));
        if(sequence.getCurrentId() == 9999)
            sequence.setCurrentId(0L);
        sequence.setCurrentId(sequence.getCurrentId()+1);
        repository.save(sequence);
        return String.format("%04d", sequence.getCurrentId());
    }


}
