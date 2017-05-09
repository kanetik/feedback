package io.rverb.feedback.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.rverb.feedback.data.api.interceptor.ApiKeyInterceptor;
import io.rverb.feedback.data.api.interceptor.LoggingInterceptor;
import io.rverb.feedback.data.api.interceptor.UserAgentInterceptor;
import okhttp3.OkHttpClient;

class ApiUtils {
    @NonNull
    static OkHttpClient getOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new ApiKeyInterceptor(context));
        builder.addInterceptor(new UserAgentInterceptor(context));

        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.addNetworkInterceptor(new LoggingInterceptor());

        return builder.build();
    }
}