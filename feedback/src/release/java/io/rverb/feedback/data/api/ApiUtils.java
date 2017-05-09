package io.rverb.feedback.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class ApiUtils {
    @NonNull
    public static OkHttpClient getOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new UserAgentInterceptor());
        builder.addInterceptor(new ApiKeyInterceptor(context));

        if (RverbioUtils.isDebug(context)) {
            builder.addNetworkInterceptor(new StethoInterceptor());
            builder.addNetworkInterceptor(new LoggingInterceptor());
        }

        return builder.build();
    }
}