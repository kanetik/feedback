package io.rverb.feedback.data.api;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import io.rverb.feedback.R;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.FileRequestBody;
import io.rverb.feedback.model.IPersistable;
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
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    static IPersistable post(final Context context, final IPersistable data) {
        OkHttpClient client = ApiUtils.getOkHttpClient(context);

        Gson gson = new Gson();
        String json = gson.toJson(data);

        if (RverbioUtils.isDebug(context)) {
            LogUtils.d("POST " + data.getDataTypeDescriptor() + " - " + json);
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        String url = context.getString(R.string.api_base_url) + data.getDataTypeDescriptor();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            try {
                if (response.isSuccessful() && responseBody != null) {
                    return DataUtils.fromJson(responseBody.string(), data.getClass());
                } else if (RverbioUtils.isDebug(context)) {
                    LogUtils.d("POST " + data.getDataTypeDescriptor() + " Failed: " + response.message());
                }
            } catch (Exception e) {
                if (RverbioUtils.isDebug(context)) {
                    LogUtils.d("POST " + data.getDataTypeDescriptor() + " Exception: " + e.getMessage());
                }
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (IOException e) {
            if (RverbioUtils.isDebug(context)) {
                LogUtils.d("POST " + data.getDataTypeDescriptor() + " IOException: " + e.getMessage());
            }
        }

        return null;
    }

    static boolean insertEndUser(final Context context, final EndUser endUser) {
        OkHttpClient client = ApiUtils.getOkHttpClient(context);

        if (RverbioUtils.isDebug(context)) {
            LogUtils.d("Insert endUser - " + endUser.endUserId);
        }

        Gson gson = new Gson();
        String json = gson.toJson(endUser);

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        String url = context.getString(R.string.api_base_url) + "enduser";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                if (RverbioUtils.isDebug(context)) {
                    LogUtils.d("Insert EndUser Failed: " + response.message());
                }

                return false;
            }
        } catch (IOException e) {
            if (RverbioUtils.isDebug(context)) {
                LogUtils.d("Insert EndUser IOException: " + e.getMessage());
            }

            return false;
        }

        return true;
    }

    static boolean updateEndUser(final Context context, final EndUser endUser) {
        OkHttpClient client = ApiUtils.getOkHttpClient(context);

        if (RverbioUtils.isDebug(context)) {
            LogUtils.d("Update endUser - " + endUser.endUserId);
        }

        Gson gson = new Gson();
        String json = gson.toJson(endUser);

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

        String url = context.getString(R.string.api_base_url) + "enduser";
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                if (RverbioUtils.isDebug(context)) {
                    LogUtils.d("Update EndUser Failed: " + response.message());
                }

                return false;
            }
        } catch (IOException e) {
            if (RverbioUtils.isDebug(context)) {
                LogUtils.d("Update EndUser IOException: " + e.getMessage());
            }

            return false;
        }

        return true;
    }

    static void putFile(Context context, File file, String url) {
        if (RverbioUtils.isDebug(context)) {
            LogUtils.d("Uploading " + file.getName() + " to " + url);
        }

        OkHttpClient client = ApiUtils.getOkHttpClient(context);
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
                    if (RverbioUtils.isDebug(context)) {
                        LogUtils.d("PUT FILE Succeeded - " + response.request().url());
                    }

                    file.delete();
                } else if (RverbioUtils.isDebug(context)) {
                    LogUtils.d("PUT FILE Failed - " + response.message());
                }
            } catch (Exception e) {
                if (RverbioUtils.isDebug(context)) {
                    LogUtils.d("PUT FILE Error 1: " + e.getMessage());
                }
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (IOException e) {
            if (RverbioUtils.isDebug(context)) {
                LogUtils.d("PUT FILE IOException: " + e.getMessage());
            }
        }
    }
}
