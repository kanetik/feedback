package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

import io.rverb.feedback.data.api.FeedbackService;
import io.rverb.feedback.utility.DateUtils;

public class Feedback implements Cacheable {
    static final long serialVersionUID = 325L;

    private transient String screenshotFileName;

    public String applicationId;
    public String sessionId;
    public String endUserId;
    public String timestamp;
    public String feedbackType;
    public Map<String, String> contextData;
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
