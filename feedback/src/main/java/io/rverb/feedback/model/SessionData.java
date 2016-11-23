package io.rverb.feedback.model;

import android.text.TextUtils;

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

    public SessionData(String appId, String sessionId, String supportId, String userIdentifier) {
        this.appId = appId;
        this.sessionId = sessionId;
        this.supportId = supportId;
        this.sessionStartUtc = DateUtils.nowUtc();

        if (!TextUtils.isEmpty(userIdentifier)) {
            this.userIdentifier = userIdentifier;
        }
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | SupportId: " + supportId + " | UserIdentifier: "
                + userIdentifier + " | SessionStartUTC" + sessionStartUtc;

    }
}
