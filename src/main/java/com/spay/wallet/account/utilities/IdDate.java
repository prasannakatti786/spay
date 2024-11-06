package com.spay.wallet.account.utilities;


import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class IdDate {

    public LocalDate getCurrentDate(){
        return LocalDate.now();
    }
}
