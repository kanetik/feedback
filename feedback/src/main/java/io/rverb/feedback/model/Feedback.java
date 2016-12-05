package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

import io.rverb.feedback.data.api.FeedbackService;
import io.rverb.feedback.utility.DateUtils;

public class Feedback implements Cacheable {
    private static final long serialVersionUID = 325L;

    private String screenshotFileName;

    public String sessionId;
    public String appVersion;
    public String locale;
    public String deviceName;
    public String make;
    public String model;
    public String osVersion;
    public String networkType;
    public String timestamp;
    public String comment;
    public Map<String, String> contextData;
    public String uploadUrl;

    public Feedback(String sessionId) {
        this.sessionId = sessionId;
        this.timestamp = DateUtils.nowUtc();
    }

    public Feedback(String sessionId, String comment) {
        this.sessionId = sessionId;
        this.comment = comment;
        this.timestamp = DateUtils.nowUtc();
    }

    public Feedback(String sessionId, String comment, String screenshotFileName) {
        this.sessionId = sessionId;
        this.comment = comment;
        this.screenshotFileName = screenshotFileName;
        this.timestamp = DateUtils.nowUtc();
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | Comment: " + comment;
    }

    @Override
    public String getDataTypeDescriptor() {
        return "feedback";
    }

    @Override
    public Intent getServiceIntent(Context context, String cacheFileName) {
        Intent serviceIntent = new Intent(context, FeedbackService.class);

        serviceIntent.putExtra("temp_file_name", cacheFileName);
        serviceIntent.putExtra("data", this);
        serviceIntent.putExtra("screenshot_file_name", this.screenshotFileName);

        return serviceIntent;
    }
}
