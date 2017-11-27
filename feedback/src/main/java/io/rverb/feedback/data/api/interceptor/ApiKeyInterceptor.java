package io.rverb.feedback.data.api.interceptor;

import android.text.TextUtils;

import java.io.IOException;

import io.rverb.feedback.Rverbio;
import io.rverb.feedback.utility.RverbioUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String apiKey = Rverbio.getApiKey();

        if (!TextUtils.isEmpty(apiKey)) {
            Request requestWithApiKey = originalRequest.newBuilder()
                    .header("apiKey", apiKey)
                    .build();

            return chain.proceed(requestWithApiKey);
        }

        return chain.proceed(originalRequest);
    }
}