package io.rverb.feedback.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import java.util.ArrayList;

import io.rverb.feedback.data.api.FeedbackService;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.DateUtils;
import io.rverb.feedback.utility.RverbioUtils;

public class Feedback implements Persistable {
    static final long serialVersionUID = 325L;

    public static String TYPE_DESCRIPTOR = "feedback";

    private String screenshotFileName;

    public String applicationId;
    public String sessionId;
    public String endUserId;
    public String timestamp;
    public String feedbackType;
    public ArrayList<DataItem> contextData;
    public String comment;
    public String appVersion;
    public String locale;
    public String deviceName;
    public String deviceManufacturer;
    public String deviceModel;
    public String osVersion;
    public String networkType;
    public String uploadUrl;

    public Feedback(String applicationId, String sessionId, String endUserId, String feedbackType, String comment, String screenshotFileName) {
        this.applicationId = applicationId;
        this.sessionId = sessionId;
        this.endUserId = endUserId;
        this.feedbackType = feedbackType;
        this.comment = comment;
        this.screenshotFileName = screenshotFileName;
        this.timestamp = DateUtils.nowUtc();
        this.contextData = new ArrayList<>();
    }

    @Override
    public String getDataTypeDescriptor() {
        return TYPE_DESCRIPTOR;
    }

    @Override
    public Intent getPersistServiceIntent(Context context, ResultReceiver resultReceiver) {
        Intent serviceIntent = new Intent(context, FeedbackService.class);

        serviceIntent.putExtra(DataUtils.EXTRA_RESULT_RECEIVER, resultReceiver);
        serviceIntent.putExtra(DataUtils.EXTRA_SELF, this);
        serviceIntent.putExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME, this.screenshotFileName);

        return serviceIntent;
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | Comment: " + comment;
    }
}
