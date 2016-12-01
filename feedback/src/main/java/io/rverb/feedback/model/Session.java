package io.rverb.feedback.model;

import java.io.Serializable;

import io.rverb.feedback.utility.DateUtils;

public class Session implements Serializable {
    private static final long serialVersionUID = 348L;

    public String sessionId;
    public String supportId;
    public String sessionStartUtc;

    public Session(String sessionId, String supportId) {
        this.sessionId = sessionId;
        this.supportId = supportId;
        this.sessionStartUtc = DateUtils.nowUtc();
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | SupportId: " + supportId
                + " | SessionStartUTC: " + sessionStartUtc;

    }
}
