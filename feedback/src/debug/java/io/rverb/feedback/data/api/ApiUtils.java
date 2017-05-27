package io.rverb.feedback.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import io.rverb.feedback.data.api.interceptor.ApiKeyInterceptor;
import io.rverb.feedback.data.api.interceptor.LoggingInterceptor;
import io.rverb.feedback.data.api.interceptor.UserAgentInterceptor;
import okhttp3.OkHttpClient;

class ApiUtils {
    @NonNull
    static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInterceptor())
                .addInterceptor(new UserAgentInterceptor())
                .addInterceptor(new LoggingInterceptor())
                .build();
    }
}