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

    public static long currentInMillisUtc() {
        int gmtOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        return System.currentTimeMillis() - gmtOffset;
    }

    public static String millisToDate(long millis) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return df.format(calendar.getTime());
    }

    public static long weekAgoInMillis() {
        return System.currentTimeMillis() - android.text.format.DateUtils.WEEK_IN_MILLIS;
    }
}
