package io.rverb.feedback.data.api.interceptor;

import android.os.Build;

import java.io.IOException;

import io.rverb.feedback.BuildConfig;
import io.rverb.feedback.Rverbio;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

public class UserAgentInterceptor implements Interceptor {
    public static final String HEADER_KEY_USER_AGENT = "User-Agent";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String uaString =
                "Rverbio SDK/" + BuildConfig.VERSION_NAME + " " +
                        "(Android " + Build.VERSION.RELEASE + ")";

        if (Rverbio.getInstance().getOptions().isDebugMode()) {
            uaString += " DEBUG";
        }

        Request requestWithUserAgent = originalRequest.newBuilder()
                .header(HEADER_KEY_USER_AGENT, toSafeAscii(uaString))
                .build();

        return chain.proceed(requestWithUserAgent);
    }

    private String toSafeAscii(String s) {
        for (int i = 0, length = s.length(), c; i < length; i += Character.charCount(c)) {
            c = s.codePointAt(i);
            if (c > '\u001f' && c < '\u007f') {
                continue;
            }

            Buffer buffer = new Buffer();
            buffer.writeUtf8(s, 0, i);
            for (int j = i; j < length; j += Character.charCount(c)) {
                c = s.codePointAt(j);
                buffer.writeUtf8CodePoint(c > '\u001f' && c < '\u007f' ? c : '?');
            }

            return buffer.readUtf8();
        }

        return s;
    }
}

