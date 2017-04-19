package io.rverb.feedback.data.api;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.rverb.feedback.R;
import io.rverb.feedback.model.Cacheable;
import io.rverb.feedback.model.FileRequestBody;
import io.rverb.feedback.model.Patch;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.LogUtils;
import io.rverb.feedback.utility.RverbioUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

class ApiManager {
    private static String API_KEY_HEADER_NAME = "apiKey";

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    //, Class<? extends Cacheable> responseType
    static void post(final Context context, final String tempFileName, final Cacheable data) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(data);

        LogUtils.d("POST " + data.getDataTypeDescriptor() + " - " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        String url = context.getString(R.string.api_base_url) + data.getDataTypeDescriptor();
        Request request = new Request.Builder()
                .addHeader(API_KEY_HEADER_NAME, RverbioUtils.getApiKey(context))
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            try {
                // Delete the temp file, if it exists
                if (response.isSuccessful()) {
                    DataUtils.deleteFile(tempFileName);
                } else {
                    LogUtils.d("POST " + data.getDataTypeDescriptor() + " Failed: " + response.message());
                }
            } catch (Exception e) {
                LogUtils.d("POST " + data.getDataTypeDescriptor() + " Exception: " + e.getMessage());
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (IOException e) {
            LogUtils.d("POST " + data.getDataTypeDescriptor() + " IOException: " + e.getMessage());
        }
    }

    static Cacheable postWithResponse(final Context context, final String tempFileName, final Cacheable data) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(data);

        LogUtils.d("POST " + data.getDataTypeDescriptor() + " - " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        String url = context.getString(R.string.api_base_url) + data.getDataTypeDescriptor();
        Request request = new Request.Builder()
                .addHeader(API_KEY_HEADER_NAME, RverbioUtils.getApiKey(context))
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            try {
                // Delete the temp file, if it exists
                if (response.isSuccessful()) {
                    DataUtils.deleteFile(tempFileName);

                    if (responseBody != null) {
                        return DataUtils.fromJson(responseBody.string(), data.getClass());
                    }
                } else {
                    LogUtils.d("POST " + data.getDataTypeDescriptor() + " Failed: " + response.message());
                }
            } catch (Exception e) {
                LogUtils.d("POST " + data.getDataTypeDescriptor() + " Exception: " + e.getMessage());
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (IOException e) {
            LogUtils.d("POST " + data.getDataTypeDescriptor() + " IOException: " + e.getMessage());
        }

        return null;
    }

    static void patch(final Context context, final String tempFileName, final String endpoint, final List<Patch> patches, final String id) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        Gson gson = new Gson();
        String json = gson.toJson(patches);

        LogUtils.d("PATCH " + endpoint + " - " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        String url = context.getString(R.string.api_base_url) + endpoint + "?id=" + id;
        Request request = new Request.Builder()
                .addHeader(API_KEY_HEADER_NAME, RverbioUtils.getApiKey(context))
                .url(url)
                .patch(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            try {
                // Delete the temp file, if it exists
                if (response.isSuccessful()) {
                    DataUtils.deleteFile(tempFileName);
                } else {
                    String message = "PATCH " + endpoint + " Failed (1) - " + response.message();
                    if (AppUtils.crashlyticsCapable()) {
                        //TODO: Remove Crashlytics references
                        Crashlytics.logException(new Throwable(message));
                    }
                }
            } catch (Exception e) {
                String message = "PATCH " + endpoint + " Failed (2) - " + response.message();
                if (AppUtils.crashlyticsCapable()) {
                    Crashlytics.logException(new Throwable(message, e));
                }
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (IOException e) {
            String message = "PATCH " + endpoint + " Failed (3) - " + e.getMessage();
            if (AppUtils.crashlyticsCapable()) {
                Crashlytics.logException(new Throwable(message, e));
            }
        }
    }

    static void putFile(File file, String url) {
        LogUtils.d("Uploading " + file.getName() + " to " + url);

        OkHttpClient client = ApiUtils.getOkHttpClient();
        RequestBody requestBody = new FileRequestBody(file, "image/jpeg");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-ms-blob-type", "BlockBlob")
                .put(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            try {
                // Delete the temp file, if it exists
                if (response.isSuccessful()) {
                    LogUtils.d("PUT FILE Succeeded - " + response.request().url());
                    file.delete();
                } else {
                    LogUtils.d("PUT FILE Failed - " + response.message());
                }
            } catch (Exception e) {
                LogUtils.d("PUT FILE Error 1: " + e.getMessage());
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (IOException e) {
            LogUtils.d("PUT FILE IOException: " + e.getMessage());
        }
    }
}
