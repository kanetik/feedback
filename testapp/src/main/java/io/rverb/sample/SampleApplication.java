package io.rverb.sample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.Stetho;

import java.util.Locale;
import java.util.UUID;

import io.rverb.feedback.Rverbio;
import io.rverb.feedback.RverbioOptions;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        Rverbio.initialize(this, new RverbioOptions().setDebugMode(BuildConfig.DEBUG));
        Rverbio.getInstance().setUserIdentifier(getSupportId(this));
    }

    public static String getSupportId(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String supportId = prefs.getString("support_id", "");
        if (TextUtils.isEmpty(supportId)) {
            supportId = UUID.randomUUID().toString();
            prefs.edit().putString("support_id", supportId).apply();
        }

        Log.d("EndUser", String.format(Locale.getDefault(), "%1$s: %2$s", "SupportId", supportId));

        return supportId;
    }
}
