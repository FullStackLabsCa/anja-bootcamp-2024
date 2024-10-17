package io.reactivestax.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {

    private  static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized Timestamp formattedTimestamp(){
        return Timestamp.valueOf(LocalDateTime.now().format(formatter));
    }
}
