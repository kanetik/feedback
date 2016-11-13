package io.rverb.feedback.model;

import android.text.TextUtils;

import java.io.Serializable;

public class SessionData implements Serializable {
    public String sessionId;
    public String supportId;
    public String userIdentifier;

    public SessionData(String sessionId, String supportId) {
        this.sessionId = sessionId;
        this.supportId = supportId;
    }

    public SessionData(String sessionId, String supportId, String userIdentifier) {
        this.sessionId = sessionId;
        this.supportId = supportId;

        if (!TextUtils.isEmpty(userIdentifier)) {
            this.userIdentifier = userIdentifier;
        }
    }
}
