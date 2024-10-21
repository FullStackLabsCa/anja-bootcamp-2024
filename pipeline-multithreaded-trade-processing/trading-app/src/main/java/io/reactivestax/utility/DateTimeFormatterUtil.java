package io.reactivestax.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeFormatterUtil() {
    }

    public static synchronized Timestamp formattedTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now().format(formatter));
    }
}
