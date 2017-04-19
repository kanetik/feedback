package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;

import io.rverb.feedback.model.Cacheable;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.utility.AppUtils;
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

        Serializable feedbackObject = intent.getSerializableExtra(DataUtils.EXTRA_DATA);
        String tempFileName = intent.getStringExtra(DataUtils.EXTRA_TEMPORARY_FILE_NAME);
        String screenshotFileName = intent.getStringExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME);

        if (feedbackObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(feedbackObject instanceof Feedback)) {
            throw new ClassCastException("Intent's data object is not the expected type (Feedback)");
        }

        Feedback feedback = (Feedback) feedbackObject;
        postFeedback(feedback, tempFileName, screenshotFileName);
    }

    void postFeedback(Feedback feedback, String tempFileName, String screenshotFileName) {
        Cacheable response = ApiManager.postWithResponse(this, tempFileName, feedback);
        if (response != null && response instanceof Feedback) {
            Feedback feedbackResponse = (Feedback) response;

            if (!RverbioUtils.isNullOrWhiteSpace(screenshotFileName) && !RverbioUtils.isNullOrWhiteSpace(feedbackResponse.uploadUrl)) {
                final File screenshot = new File(screenshotFileName);
                if (screenshot.exists()) {
                    ApiManager.putFile(screenshot, feedbackResponse.uploadUrl);
                }
            }

            EndUser endUser = RverbioUtils.getEndUser(this);
            if (endUser != null && !RverbioUtils.isNullOrWhiteSpace(endUser.emailAddress)) {
                AppUtils.notifyUser(this, AppUtils.FEEDBACK_SUBMITTED);
            } else {
                AppUtils.notifyUser(this, AppUtils.ANONYMOUS_FEEDBACK_SUBMITTED);
            }
        }
    }
}
