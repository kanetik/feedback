package io.rverb.feedback.data.api;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.Cacheable;
import io.rverb.feedback.model.Patch;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.LogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

class ApiManager {
    private static String API_ROOT = "https://www.rverb.io/api/";
    private static String API_KEY_HEADER_NAME = "apiKey";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static void post(final Context context, final String tempFileName, final Cacheable data) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(data);

        LogUtils.d("POST " + data.getDataTypeDescriptor() + " - " + json);
        RequestBody body = RequestBody.create(JSON, json);

        String url = API_ROOT + data.getDataTypeDescriptor();
        Request request = new Request.Builder()
                .addHeader(API_KEY_HEADER_NAME, AppUtils.getApiKey(context))
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            try {
                // Delete the temp file, if it exists
                if (response.isSuccessful()) {
                    if (responseBody != null) {
                        LogUtils.d("Response: " + responseBody.string());
                    }

                    RverbioUtils.deleteFile(tempFileName);
                } else {
                    LogUtils.d("POST " + data.getDataTypeDescriptor() + " Failed: " + response.message());
                }
            } catch (Exception e) {
                LogUtils.d("POST " + data.getDataTypeDescriptor() + " Error 1: " + e.getMessage());
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (IOException e) {
            LogUtils.d("POST " + data.getDataTypeDescriptor() + " Error 2: " + e.getMessage());
        }
    }

    static void patch(final Context context, final String tempFileName, final String endpoint, final List<Patch> patches, final String id) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(patches);

        LogUtils.d("PATCH " + endpoint + " - " + json);
        RequestBody body = RequestBody.create(JSON, json);

        String url = API_ROOT + endpoint + "?id=" + id;
        Request request = new Request.Builder()
                .addHeader(API_KEY_HEADER_NAME, AppUtils.getApiKey(context))
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
