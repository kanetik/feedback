package io.rverb.feedback.data.api;

import okhttp3.OkHttpClient;

public class ApiUtils {
    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }
}