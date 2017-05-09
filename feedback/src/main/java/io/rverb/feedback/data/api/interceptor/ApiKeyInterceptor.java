package io.rverb.feedback.data.api.interceptor;

import android.content.Context;

import java.io.IOException;

import io.rverb.feedback.utility.RverbioUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptor implements Interceptor {
    private static String API_KEY_HEADER_NAME = "apiKey";

    private Context _context;

    public ApiKeyInterceptor(Context context) {
        _context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request requestWithApiKey = originalRequest.newBuilder()
                .header(API_KEY_HEADER_NAME, RverbioUtils.getApiKey(_context))
                .build();

        return chain.proceed(requestWithApiKey);
    }
}