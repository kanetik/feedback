package com.kanetik.feedback.network;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import com.kanetik.feedback.model.Feedback;

import java.io.Serializable;

public class FeedbackService extends IntentService {
    public FeedbackService() {
        super("FeedbackService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Serializable feedbackObject = intent.getSerializableExtra(Feedback.EXTRA_SELF);

        if (feedbackObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(feedbackObject instanceof Feedback)) {
            throw new ClassCastException("Intent's data object is not the expected type (KanetikFeedback)");
        }

        Feedback feedback = (Feedback) feedbackObject;

        // TODO: Make the sender configurable
        Sender sender = new MailJetSender(this);
        boolean isSent = sender.send(feedback);

        ResultReceiver resultReceiver = null;
        if (intent.hasExtra(Feedback.EXTRA_RESULT_RECEIVER)) {
            resultReceiver = intent.getParcelableExtra(Feedback.EXTRA_RESULT_RECEIVER);
        }

        if (resultReceiver != null) {
            if (isSent) {
                resultReceiver.send(Activity.RESULT_OK, null);
            } else {
                resultReceiver.send(Activity.RESULT_CANCELED, null);
            }
        }
    }
}
