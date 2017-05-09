package io.rverb.feedback.data.api.interceptor;

import android.content.Context;
import android.os.Build;

import java.io.IOException;

import io.rverb.feedback.BuildConfig;
import io.rverb.feedback.utility.RverbioUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {
    public static final String HEADER_KEY_USER_AGENT = "User-Agent";

    private Context _context;

    public UserAgentInterceptor(Context context) {
        _context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String uaString =
                "Rverbio SDK/" + BuildConfig.VERSION_NAME + " " +
                        "(Android " + Build.VERSION.RELEASE + ")";

        if (RverbioUtils.isDebug(_context)) {
            uaString += " DEBUG";
        }

        Request requestWithUserAgent = originalRequest.newBuilder()
                .header(HEADER_KEY_USER_AGENT, uaString)
                .build();

        return chain.proceed(requestWithUserAgent);
    }
}

