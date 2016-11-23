package io.rverb.feedback.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
    public static String now() {
        Calendar c = GregorianCalendar.getInstance();
        DateFormat df = SimpleDateFormat.getDateTimeInstance();
        return df.format(c.getTime());
    }

    public static String nowUtc() {
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        return df.format(new Date());
    }
}
