package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import io.rverb.feedback.Rverbio;
import io.rverb.feedback.utility.RverbioUtils;
import io.rverb.feedback.model.Cacheable;
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
        String screenshotFileName = intent.getStringExtra("screenshot_file_name");

        if (feedbackObject == null) {
            throw new NullPointerException("Intent feedback object is null");
        }

        if (!(feedbackObject instanceof Feedback)) {
            throw new ClassCastException("Intent feedback object is not the expected type (Feedback)");
        }

        Feedback feedback = (Feedback) feedbackObject;
        postFeedback(feedback, tempFileName, screenshotFileName);
    }

    void postFeedback(Feedback feedback, String tempFileName, String screenshotFileName) {
        addSystemData(feedback);

        // TODO: Allow dev to add to a collection that will persist throughout the session and submit with the feedback

        Cacheable response = ApiManager.postWithResponse(this, tempFileName, feedback);
        if (response != null && response instanceof Feedback && !RverbioUtils.isNullOrWhiteSpace(screenshotFileName)) {
            Feedback feedbackResponse = (Feedback)response;
            if (!RverbioUtils.isNullOrWhiteSpace(feedbackResponse.uploadUrl)) {
                final File screenshot = new File(screenshotFileName);
                if (screenshot.exists()) {
                    ApiManager.putFile(screenshot, feedbackResponse.uploadUrl);
                }
            }
        }
    }

    void addSystemData(Feedback feedback) {
        Map<String, String> data = RverbioUtils.getExtraData(this);

        feedback.appVersion = data.get(RverbioUtils.EXTRA_DATA_APP_VERSION);
        feedback.locale = data.get(RverbioUtils.EXTRA_DATA_LOCALE);
        feedback.make = data.get(RverbioUtils.EXTRA_DATA_MAKE);
        feedback.model = data.get(RverbioUtils.EXTRA_DATA_MODEL);
        feedback.deviceName = data.get(RverbioUtils.EXTRA_DATA_DEVICE_NAME);
        feedback.osVersion = data.get(RverbioUtils.EXTRA_DATA_OS_VERSION);
        feedback.networkType = data.get(RverbioUtils.EXTRA_DATA_NETWORK_TYPE);
    }
}
