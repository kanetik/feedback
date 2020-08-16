package com.kanetik.feedback.network;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import androidx.annotation.Keep;

import com.kanetik.feedback.model.Feedback;

import java.io.Serializable;

import kotlinx.serialization.json.Json;

@Keep
public class FeedbackService extends IntentService {
    public FeedbackService() {
        super("FeedbackService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Feedback feedbackObject = Json.Default.decodeFromString(Feedback.Companion.getSerializer(), intent.getStringExtra(Feedback.EXTRA_SELF));

        if (feedbackObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        // TODO: Make the sender configurable
        Sender sender = new MailJetSender(this);
        boolean isSent = sender.send(feedbackObject);

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
