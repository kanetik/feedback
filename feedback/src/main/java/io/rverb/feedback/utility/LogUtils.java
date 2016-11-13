package io.rverb.feedback.utility;

import android.util.Log;

import java.util.Locale;

public class LogUtils {
    private static final String LOG_TAG = "rverbio";

    public static void d(String message) {
        Log.d(LOG_TAG, message);
    }

    public static void d(String key, String value) {
        Log.d(LOG_TAG, String.format(Locale.getDefault(), "%1$s: %2$s", key, value));
    }

    public static void w(String message, Throwable t) {
        Log.w(LOG_TAG, message, t);
    }
}
