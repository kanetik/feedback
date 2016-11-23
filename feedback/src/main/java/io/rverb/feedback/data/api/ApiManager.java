package io.rverb.feedback.data.api;

import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.SessionData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class ApiManager {
    private static String API_ROOT = "https://www.rverb.io/api/";
    private static String API_KEY = "uy/UOQSmqM9ZfnCxBfvSI6VRw7HM4en2L80SAuKqBOGz2V/9MYN3sQ==";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static void postSession(SessionData session, String tempFileName) {
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

        post("session", paramJson, tempFileName);
    }

    static void post(String endpoint, JSONObject paramJson, String tempFileName) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        RequestBody body = RequestBody.create(JSON, paramJson.toString());

        String url = API_ROOT + endpoint + "?code=" + API_KEY;
        Request request = new Request.Builder()
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
