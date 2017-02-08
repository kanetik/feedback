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
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));

        Calendar c = GregorianCalendar.getInstance();
        return df.format(c.getTime());
    }
}
