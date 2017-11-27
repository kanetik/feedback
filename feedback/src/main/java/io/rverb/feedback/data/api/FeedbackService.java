package io.rverb.feedback.data.api;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.Serializable;

import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.model.IPersistable;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.RverbioUtils;

public class FeedbackService extends IntentService {
    public FeedbackService() {
        super("FeedbackService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Crashlytics.log(Log.INFO, "Rverbio", "FeedbackService Intent: " + intent.toString());

        Serializable feedbackObject = intent.getSerializableExtra(DataUtils.EXTRA_SELF);
        String screenshotFileName = intent.getStringExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME);

        if (feedbackObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(feedbackObject instanceof Feedback)) {
            throw new ClassCastException("Intent's data object is not the expected type (Feedback)");
        }

        Feedback feedback = (Feedback) feedbackObject;
        IPersistable response = ApiManager.post(this, feedback);

        ResultReceiver resultReceiver = null;
        if (intent.hasExtra(DataUtils.EXTRA_RESULT_RECEIVER)) {
            resultReceiver = intent.getParcelableExtra(DataUtils.EXTRA_RESULT_RECEIVER);
        }

        if (resultReceiver != null) {
            if (response != null && response instanceof Feedback) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(DataUtils.EXTRA_RESULT, response);
                resultReceiver.send(Activity.RESULT_OK, bundle);
            } else {
                resultReceiver.send(Activity.RESULT_CANCELED, null);
            }
        }

        if (response != null && response instanceof Feedback) {
            Feedback feedbackResponse = (Feedback) response;

            if (!RverbioUtils.isNullOrWhiteSpace(screenshotFileName) && !RverbioUtils.isNullOrWhiteSpace(feedbackResponse.uploadUrl)) {
                final File screenshot = new File(screenshotFileName);
                if (screenshot.exists()) {
                    ApiManager.putFile(this, screenshot, feedbackResponse.uploadUrl);
                }
            }
        }
    }
}
