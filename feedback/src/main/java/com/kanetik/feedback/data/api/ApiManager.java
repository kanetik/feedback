package com.kanetik.feedback.data.api;

import android.content.Context;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.model.Feedback;
import com.kanetik.feedback.utility.LogUtils;

import okhttp3.MediaType;
import okhttp3.RequestBody;

class ApiManager {
    static Feedback post(final Context context, final Feedback feedback) {
//        OkHttpClient client = ApiUtils.getOkHttpClient();

        String json = feedback.toJson();

        if (KanetikFeedback.isDebug()) {
            LogUtils.i("POST KanetikFeedback - " + json);
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);

//        String url = context.getString(R.string.kanetik_feedback_api_base_url) + feedback.getDataTypeDescriptor();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//            ResponseBody responseBody = response.body();
//
//            try {
//                if (response.isSuccessful() && responseBody != null) {
//                    return feedback.fromJson(responseBody.string());
//                } else if (KanetikFeedback.isDebug()) {
//                    LogUtils.i("POST " + feedback.getDataTypeDescriptor() + " Failed: " + response.message());
//                }
//            } catch (Exception e) {
//                if (KanetikFeedback.isDebug()) {
//                    LogUtils.i("POST " + feedback.getDataTypeDescriptor() + " Exception: " + e.getMessage());
//                }
//            } finally {
//                if (responseBody != null) {
//                    responseBody.close();
//                }
//            }
//        } catch (IOException e) {
//            if (KanetikFeedback.isDebug()) {
//                LogUtils.i("POST " + feedback.getDataTypeDescriptor() + " IOException: " + e.getMessage());
//            }
//        }

        return null;
    }
}
