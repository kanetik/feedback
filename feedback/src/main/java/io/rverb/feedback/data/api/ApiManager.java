package io.rverb.feedback.data.api;

import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.SessionData;
import io.rverb.feedback.utility.LogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class ApiManager {
    private static String API_ROOT = "https://www.rverb.io/api/";
    private static String API_KEY_HEADER_NAME = "apiKey";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static void postSession(String apiKey, SessionData session, String tempFileName) {
        Map<String, String> params = new ArrayMap<>();

        params.put("ApplicationId", session.appId);
        params.put("SessionId", session.sessionId);
        params.put("SupportId", session.supportId);

        String userIdentifier = session.userIdentifier;
        if (userIdentifier != null) {
            params.put("UserIdentifier", session.userIdentifier);
        }

        params.put("SessionStartUtc", session.sessionStartUtc);

        JSONObject paramJson = new JSONObject(params);

        post(apiKey, "session", paramJson, tempFileName);
    }

    static void post(String apiKey, String endpoint, JSONObject paramJson, String tempFileName) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        LogUtils.d("POST " + endpoint + " - " + paramJson.toString());

        RequestBody body = RequestBody.create(JSON, paramJson.toString());

        String url = API_ROOT + endpoint;
        Request request = new Request.Builder()
                .addHeader(API_KEY_HEADER_NAME, apiKey)
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // Delete the temp file, if it exists
            if (response.isSuccessful()) {
                RverbioUtils.deleteFile(tempFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
