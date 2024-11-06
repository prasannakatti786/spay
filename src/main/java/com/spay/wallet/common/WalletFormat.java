package com.spay.wallet.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WalletFormat {
    public static String formatDateTime(LocalDateTime dateTime){
        var date =  formatDateAndTime(dateTime);
        return date.get("DATE")+" "+date.get("TIME")+" 0+GMT";
    }

    public static String currencyFormat(BigDecimal amount){
         DecimalFormat df = new DecimalFormat("#,##0.00");
       return df.format(amount.setScale(2, RoundingMode.HALF_DOWN));
    }

    private static Map<String,String> formatDateAndTime(LocalDateTime dateTime){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        return Map.of("DATE", dateTime.format(dateFormatter),
                "TIME",dateTime.format(timeFormatter));
    }
}
