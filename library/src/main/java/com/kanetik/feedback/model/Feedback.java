package com.kanetik.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.kanetik.feedback.network.FeedbackService;
import com.kanetik.feedback.utility.FeedbackUtils;

import java.io.Serializable;

import androidx.annotation.Keep;

@Keep
public class Feedback implements Serializable {
    static final long serialVersionUID = 325L;

    public static final String EXTRA_RESULT_RECEIVER = "result_receiver";
    public static final String EXTRA_SELF = "data";

    public ContextData appData;
    public ContextData deviceData;
    public ContextData devData;

    public String comment;
    public String from;

    private int getRetryLimit() {
        return 1;
    }

    private int retryCount;

    private int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean retryAllowed() {
        if (getRetryLimit() == 0) {
            return true;
        }

        return getRetryLimit() - getRetryCount() > 0;
    }

    public Feedback(Context context, String comment, String from) {
        FeedbackUtils.addSystemData(context, this);

        this.comment = comment;
        this.from = from;
    }

    public Intent getSendServiceIntent(Context context, ResultReceiver resultReceiver, Feedback data) {
        Intent serviceIntent = new Intent(context, FeedbackService.class);

        serviceIntent.putExtra(EXTRA_RESULT_RECEIVER, resultReceiver);
        serviceIntent.putExtra(EXTRA_SELF, data);

        return serviceIntent;
    }

    @Override
    public String toString() {
        return "Comment: " + comment + " | From: " + from;
    }
}
