package io.rverb.sample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

import io.rverb.feedback.Rverbio;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Rverbio.initialize(this, "1d482b17-5f96-41a7-98a0-4754ac11806e");
        Rverbio.getInstance().setUserIdentifier(getSupportId(this)).setDebug(BuildConfig.DEBUG).setUseNotifications(true);
    }

    public static String getSupportId(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String supportId = prefs.getString("support_id", "");
        if (TextUtils.isEmpty(supportId)) {
            supportId = UUID.randomUUID().toString();
            prefs.edit().putString("support_id", supportId).apply();
        }

        return supportId;
    }
}
