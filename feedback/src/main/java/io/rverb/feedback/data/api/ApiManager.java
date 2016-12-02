package io.rverb.feedback.data.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import io.rverb.feedback.RverbioUtils;
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
            } else {
                LogUtils.d("POST " + endpoint + " Failed - " + response.message());
            }
        } catch (IOException e) {
            LogUtils.d("POST " + endpoint + " Error - " + e.getMessage());
        }
    }

    static void patch(final String apiKey, final String endpoint, final String id, final JSONArray paramJson, final String tempFileName) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        LogUtils.d("PATCH " + endpoint + " - " + paramJson.toString());

        RequestBody body = RequestBody.create(JSON, paramJson.toString());

        String url = API_ROOT + endpoint + "?id=" + id;
        Request request = new Request.Builder()
                .addHeader(API_KEY_HEADER_NAME, apiKey)
                .url(url)
                .patch(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // Delete the temp file, if it exists
            if (response.isSuccessful()) {
                RverbioUtils.deleteFile(tempFileName);
            } else {
                LogUtils.d("PATCH " + endpoint + " Failed - " + response.message());
            }
        } catch (IOException e) {
            LogUtils.d("PATCH " + endpoint + " Error - " + e.getMessage());
        }
    }
}
