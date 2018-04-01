package com.kanetik.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.support.annotation.Keep;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kanetik.feedback.network.FeedbackService;
import com.kanetik.feedback.utility.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;

@Keep
public class Feedback implements Serializable {
    static final long serialVersionUID = 325L;

    public static final String EXTRA_RESULT_RECEIVER = "result_receiver";
    public static final String EXTRA_SELF = "data";

    public String timestampUtc;
    public ArrayList<DataItem> contextData;
    public String comment;
    public String appVersion;
    public String locale;
    public String deviceName;
    public String deviceManufacturer;
    public String deviceModel;
    public String osVersion;
    public String networkType;

    private int retryLimit = 1;

    public int getRetryLimit() {
        return retryLimit;
    }

    private int retryCount;

    public int getRetryCount() {
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

    public Feedback(String comment) {
        this.comment = comment;
        this.timestampUtc = DateUtils.nowUtc();
        this.contextData = new ArrayList<>();
    }

    public Intent getSendServiceIntent(Context context, ResultReceiver resultReceiver, Feedback data) {
        Intent serviceIntent = new Intent(context, FeedbackService.class);

        serviceIntent.putExtra(EXTRA_RESULT_RECEIVER, resultReceiver);
        serviceIntent.putExtra(EXTRA_SELF, data);

        Crashlytics.log(Log.INFO, "KanetikFeedback", "Context: " + context.toString());
        Crashlytics.log(Log.INFO, "KanetikFeedback", "KanetikFeedback Object: " + data.toString());

        return serviceIntent;
    }

    @Override
    public String toString() {
        return "Comment: " + comment;
    }

    public Feedback fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Feedback.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
