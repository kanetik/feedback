package io.rverb.feedback.model;

import java.io.Serializable;

import io.rverb.feedback.utility.DateUtils;

public class SessionData implements Serializable {
    private static final long serialVersionUID = 348L;

    public String appId;
    public String sessionId;
    public String supportId;
    public String userIdentifier;
    public String sessionStartUtc;

    public SessionData(String appId, String sessionId, String supportId) {
        this.appId = appId;
        this.sessionId = sessionId;
        this.supportId = supportId;
        this.sessionStartUtc = DateUtils.nowUtc();
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | SupportId: " + supportId
                + " | SessionStartUTC" + sessionStartUtc;

    }
}
