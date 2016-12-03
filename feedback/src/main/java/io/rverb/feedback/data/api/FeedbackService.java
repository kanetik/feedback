package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.Feedback;

public class FeedbackService extends IntentService {
    public FeedbackService() {
        super("FeedbackService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable feedbackObject = intent.getSerializableExtra("data");
        String tempFileName = intent.getStringExtra("temp_file_name");
        String apiKey = intent.getStringExtra("api_key");

        if (feedbackObject == null) {
            throw new NullPointerException("Intent feedback object is null");
        }

        if (!(feedbackObject instanceof Feedback)) {
            throw new ClassCastException("Intent feedback object is not the expected type (Feedback)");
        }

        Feedback feedback = (Feedback) feedbackObject;
        postFeedback(apiKey, feedback, tempFileName);
    }

    void postFeedback(String apiKey, Feedback feedback, String tempFileName) {
        Map<String, String> params = new ArrayMap<>();

        params.put("SessionId", feedback.sessionId);
        params.put("Comment", feedback.comment);
        params.put("Timestamp", feedback.timestamp);

        RverbioUtils.addSystemData(this, params);

        JSONObject paramJson = new JSONObject(params);

        // TODO: Allow dev to add to a collection that will persist throughout the session and submit with the feedback
//        JSONObject contextData = new JSONObject(RverbioUtils.getDevProvidedContextData());
//        try {
//            paramJson.put("ContextData", contextData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        ApiManager.post(apiKey, feedback.getTempFileNameTag(), paramJson, tempFileName);
    }
}
