package io.rverb.feedback.data.api;

import android.content.Context;

import java.io.IOException;

import io.rverb.feedback.RverbioUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class ApiManager {
    private static String API_ROOT = "https://demo0823389.mockable.io/";

    static void makeApiCall(Context context, FormBody formBody, String tempFileName) {
        OkHttpClient client = ApiUtils.getOkHttpClient();

        String url = API_ROOT + "appuser";
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
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
