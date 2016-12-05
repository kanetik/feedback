package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;

import java.io.Serializable;
import java.util.Locale;

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.utility.AppUtils;

public class FeedbackService extends IntentService {
    public FeedbackService() {
        super("FeedbackService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable feedbackObject = intent.getSerializableExtra("data");
        String tempFileName = intent.getStringExtra("temp_file_name");

        if (feedbackObject == null) {
            throw new NullPointerException("Intent feedback object is null");
        }

        if (!(feedbackObject instanceof Feedback)) {
            throw new ClassCastException("Intent feedback object is not the expected type (Feedback)");
        }

        Feedback feedback = (Feedback) feedbackObject;
        postFeedback(feedback, tempFileName);
    }

    void postFeedback(Feedback feedback, String tempFileName) {
        addSystemData(feedback);

        // TODO: Allow dev to add to a collection that will persist throughout the session and submit with the feedback

        ApiManager.post(this, tempFileName, feedback);
    }

    void addSystemData(Feedback feedback) {
        feedback.appVersion = AppUtils.getVersionName(this) + " (" + AppUtils.getVersionCode(this) + ")";
        feedback.locale = Locale.getDefault().toString();
        feedback.make = Build.MANUFACTURER;
        feedback.model = Build.MODEL;
        feedback.deviceName = Build.PRODUCT;
        feedback.osVersion = Build.VERSION.RELEASE;
        feedback.networkType = RverbioUtils.getNetworkType(this);
    }
}