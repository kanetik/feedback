package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import io.rverb.feedback.data.api.SessionService;
import io.rverb.feedback.utility.DateUtils;

public class Session implements Cacheable {
    private static final long serialVersionUID = 348L;

    public String applicationId;
    public String sessionId;
    public String endUserId;
    public String sessionStartUtc;

    public Session(String sessionId, String endUserId) {
        this.sessionId = sessionId;
        this.endUserId = endUserId;
        this.sessionStartUtc = DateUtils.nowUtc();
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | EndUserId: " + endUserId
                + " | SessionStartUTC: " + sessionStartUtc;
    }

    @Override
    public String getDataTypeDescriptor() {
        return "session";
    }

    @Override
    public Intent getServiceIntent(Context context, String cacheFileName) {
        Intent serviceIntent = new Intent(context, SessionService.class);

        serviceIntent.putExtra("temp_file_name", cacheFileName);
        serviceIntent.putExtra("data", this);

        return serviceIntent;
    }
}
