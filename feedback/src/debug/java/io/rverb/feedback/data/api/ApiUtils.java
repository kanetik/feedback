package io.rverb.feedback.data.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

class ApiUtils {
    static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.addNetworkInterceptor(new LoggingInterceptor());

        return builder.build();
    }
}