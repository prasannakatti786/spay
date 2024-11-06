package com.spay.wallet.customerservice.common.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CustomFormat {
    public static String formatDateTime(LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static String currencyFormat(BigDecimal amount){
         DecimalFormat df = new DecimalFormat("#,##0.00");
       return df.format(amount.setScale(2, RoundingMode.HALF_DOWN));
    }

}
