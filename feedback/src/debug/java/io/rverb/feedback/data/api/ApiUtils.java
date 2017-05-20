package io.rverb.feedback.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import io.rverb.feedback.data.api.interceptor.ApiKeyInterceptor;
import io.rverb.feedback.data.api.interceptor.LoggingInterceptor;
import io.rverb.feedback.data.api.interceptor.UserAgentInterceptor;
import okhttp3.OkHttpClient;

class ApiUtils {
    @NonNull
    static OkHttpClient getOkHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInterceptor(context))
                .addInterceptor(new UserAgentInterceptor(context))
                .addInterceptor(new LoggingInterceptor())
                .build();
    }
}