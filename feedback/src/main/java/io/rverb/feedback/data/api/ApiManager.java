package io.rverb.feedback.data.api;

import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Session;
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

    static void postUser(String apiKey, EndUser endUser, String tempFileName) {
        Map<String, String> params = new ArrayMap<>();

        params.put("SupportId", endUser.supportId);
        params.put("UserIdentifier", endUser.userIdentifier);
        params.put("EmailAddress", endUser.emailAddress);

        JSONObject paramJson = new JSONObject(params);

        post(apiKey, "enduser", paramJson, tempFileName);
    }

    static void postSession(String apiKey, Session session, String tempFileName) {
        Map<String, String> params = new ArrayMap<>();

        params.put("SessionId", session.sessionId);
        params.put("SupportId", session.supportId);
        params.put("SessionStartUtc", session.sessionStartUtc);

        JSONObject paramJson = new JSONObject(params);

        post(apiKey, "session", paramJson, tempFileName);
    }

    static void post(final String apiKey, final String endpoint, final JSONObject paramJson, final String tempFileName) {
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