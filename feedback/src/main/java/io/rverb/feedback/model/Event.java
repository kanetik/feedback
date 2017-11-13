package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.support.annotation.Keep;

import io.rverb.feedback.data.api.EventService;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.DateUtils;

@Keep
public class Event extends Persistable {
    static final long serialVersionUID = 302L;

    public static String TYPE_DESCRIPTOR = "event";

    public static final String EVENT_TYPE_FEEDBACK_START = "feedbackStart";
    public static final String EVENT_TYPE_FEEDBACK_CANCEL = "feedbackCancel";

    public String event;
    public String timestampUtc;

    public Event(String event) {
        this.event = event;
        this.timestampUtc = DateUtils.nowUtc();
    }

    @Override
    public String getDataTypeDescriptor() {
        return TYPE_DESCRIPTOR;
    }

    @Override
    public Intent getPersistServiceIntent(Context context, ResultReceiver resultReceiver, IPersistable data) {
        Intent serviceIntent = new Intent(context, EventService.class);

        serviceIntent.putExtra(DataUtils.EXTRA_RESULT_RECEIVER, resultReceiver);
        serviceIntent.putExtra(DataUtils.EXTRA_SELF, data);

        return serviceIntent;
    }

    @Override
    public String toString() {
        return "Event: " + event;
    }
}
