package com.rurbisservices.churchdonation.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;

public class DateUtils {
    public static Timestamp getCurrentTimestamp() {
        return Timestamp.from(Instant.now());
    }

    public static Timestamp getFromTimestampFromLocalDate(LocalDate localDate) {
        if (isObjectNull(localDate)) {
            return Timestamp.valueOf("0000-01-01 00:00:00");
        } else {
            return Timestamp.valueOf(localDate.atStartOfDay());
        }
    }

    public static Timestamp getToTimestampFromLocalDate(LocalDate localDate) {
        if (isObjectNull(localDate)) {
            return Timestamp.valueOf("3000-01-01 00:00:00");
        } else {
            return Timestamp.valueOf(localDate.atStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59));
        }
    }

    public static SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd.MM.yyyy");

    public static String convertToddMMyyyy(Timestamp timestamp) {
        return ddMMyyyy.format(timestamp.getTime());
    }

    public static LocalDate convertTimestampToLocalDate(Timestamp timestamp) {
        return timestamp.toLocalDateTime().toLocalDate();
    }
}
