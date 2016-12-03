package io.rverb.feedback.model;

import io.rverb.feedback.data.api.SessionService;
import io.rverb.feedback.utility.DateUtils;

public class Session implements Cacheable {
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

    @Override
    public String getTempFileNameTag() {
        return "session";
    }

    @Override
    public Class<?> getServiceClass() {
        return SessionService.class;
    }
}
