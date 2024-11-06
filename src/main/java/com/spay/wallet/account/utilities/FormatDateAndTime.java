package com.spay.wallet.account.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FormatDateAndTime {
    public static String formatDateTime(LocalDateTime dateTime){
        var date =  formatDateAndTime(dateTime);
        return date.get("DATE")+" "+date.get("TIME");
    }

    private static Map<String,String> formatDateAndTime(LocalDateTime dateTime){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        return Map.of("DATE", dateTime.format(dateFormatter),
                "TIME",dateTime.format(timeFormatter));
    }
}
