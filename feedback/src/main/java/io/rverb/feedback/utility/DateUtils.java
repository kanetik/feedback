package io.rverb.feedback.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static String now() {
        Calendar c = GregorianCalendar.getInstance();

        DateFormat df = SimpleDateFormat.getDateTimeInstance();
        return df.format(c.getTime());
    }

    public static String nowUtc() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        df.setTimeZone(tz);

        return df.format(new Date());
    }
}
