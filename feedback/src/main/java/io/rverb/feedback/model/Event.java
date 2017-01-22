package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

import io.rverb.feedback.data.api.EventService;
import io.rverb.feedback.utility.DateUtils;

public class Event implements Cacheable {
    private static final long serialVersionUID = 302L;

    public static final String EVENT_TYPE_FEEDBACK_START = "feedbackStart";
    public static final String EVENT_TYPE_FEEDBACK_CANCEL = "feedbackCancel";

    public String sessionId;
    public String event;
    public String timestamp;
    public Map<String, String> contextData;

    public Event(String sessionId, String event) {
        this.sessionId = sessionId;
        this.event = event;
        this.timestamp = DateUtils.nowUtc();
    }

    @Override
    public String toString() {
        return "Event: " + event;
    }

    @Override
    public String getDataTypeDescriptor() {
        return "event";
    }

    @Override
    public Intent getServiceIntent(Context context, String cacheFileName) {
        Intent serviceIntent = new Intent(context, EventService.class);

        serviceIntent.putExtra("temp_file_name", cacheFileName);
        serviceIntent.putExtra("data", this);

        return serviceIntent;
    }
}
