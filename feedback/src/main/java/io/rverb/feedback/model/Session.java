package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import io.rverb.feedback.data.api.SessionService;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.DateUtils;

public class Session implements Persistable {
    static final long serialVersionUID = 348L;

    public static String TYPE_DESCRIPTOR = "session";

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
    public String getDataTypeDescriptor() {
        return TYPE_DESCRIPTOR;
    }

    @Override
    public Intent getPersistServiceIntent(Context context, ResultReceiver resultReceiver) {
        Intent serviceIntent = new Intent(context, SessionService.class);

        serviceIntent.putExtra(DataUtils.EXTRA_RESULT_RECEIVER, resultReceiver);
        serviceIntent.putExtra(DataUtils.EXTRA_SELF, this);

        return serviceIntent;
    }

    @Override
    public String toString() {
        return "SessionId: " + sessionId + " | EndUserId: " + endUserId + " | SessionStartUTC: " + sessionStartUtc;
    }
}
