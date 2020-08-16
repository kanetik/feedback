package com.kanetik.feedback.utility;

import android.util.Log;

import androidx.annotation.Keep;

import java.util.Locale;

@Keep
public class LogUtils {
    // TODO: Debug mode only w/ debug flag (may already exist)
    private static final String LOG_TAG = "kanetik_feedback";

    public static void d(String message) {
        Log.d(LOG_TAG, message);
    }

    public static void d(String key, String value) {
        Log.d(LOG_TAG, String.format(Locale.getDefault(), "%1$s: %2$s", key, value));
    }

    public static void i(String message) {
        Log.i(LOG_TAG, message);
    }

    public static void i(String key, String value) {
        Log.i(LOG_TAG, String.format(Locale.getDefault(), "%1$s: %2$s", key, value));
    }

    public static void w(String message, Throwable t) {
        Log.w(LOG_TAG, message, t);
    }

    public static void w(String message) {
        Log.w(LOG_TAG, message);
    }
}
