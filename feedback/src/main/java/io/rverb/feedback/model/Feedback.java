package io.rverb.feedback.model;

import java.util.Map;

import io.rverb.feedback.data.api.FeedbackService;
import io.rverb.feedback.utility.DateUtils;

public class Feedback implements Cacheable {
    private static final long serialVersionUID = 325L;

    public String sessionId;
    public String appVersion;
    public String locale;
    public String deviceName;
    public String osVersion;
    public String networkType;
    public String timestamp;
    public String comment;
    public Map<String, String> contextData;

    public Feedback(String sessionId) {
        this.sessionId = sessionId;
        this.timestamp = DateUtils.nowUtc();
    }

    public Feedback(String sessionId, String comment) {
        this.sessionId = sessionId;
        this.comment = comment;
        this.timestamp = DateUtils.nowUtc();
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | Comment: " + comment;
    }

    @Override
    public String getTempFileNameTag() {
        return "feedback";
    }

    @Override
    public Class<?> getServiceClass() {
        return FeedbackService.class;
    }
}
