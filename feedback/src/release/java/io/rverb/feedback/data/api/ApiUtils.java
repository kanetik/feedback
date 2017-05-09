package io.rverb.feedback.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.rverb.feedback.data.api.interceptor.ApiKeyInterceptor;
import io.rverb.feedback.data.api.interceptor.LoggingInterceptor;
import io.rverb.feedback.data.api.interceptor.UserAgentInterceptor;
import io.rverb.feedback.utility.RverbioUtils;
import okhttp3.OkHttpClient;

public class ApiUtils {
    @NonNull
    public static OkHttpClient getOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new UserAgentInterceptor(context));
        builder.addInterceptor(new ApiKeyInterceptor(context));

        if (RverbioUtils.isDebug(context)) {
            builder.addNetworkInterceptor(new StethoInterceptor());
            builder.addNetworkInterceptor(new LoggingInterceptor());
        }

        return builder.build();
    }
}