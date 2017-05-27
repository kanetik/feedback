package io.rverb.feedback.data.api;

import android.support.annotation.NonNull;

import io.rverb.feedback.Rverbio;
import io.rverb.feedback.data.api.interceptor.ApiKeyInterceptor;
import io.rverb.feedback.data.api.interceptor.LoggingInterceptor;
import io.rverb.feedback.data.api.interceptor.UserAgentInterceptor;
import okhttp3.OkHttpClient;

public class ApiUtils {
    @NonNull
    public static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new UserAgentInterceptor());
        builder.addInterceptor(new ApiKeyInterceptor());

        if (Rverbio.getInstance().getOptions().isDebugMode()) {
            builder.addNetworkInterceptor(new LoggingInterceptor());
        }

        return builder.build();
    }
}