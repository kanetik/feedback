package io.rverb.feedback.data.api;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.Serializable;

import io.rverb.feedback.model.Event;
import io.rverb.feedback.model.IPersistable;
import io.rverb.feedback.utility.DataUtils;

public class EventService extends IntentService {
    public EventService() {
        super("EventService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        Crashlytics.log(Log.INFO, "Rverbio", "EventService Intent: " + intent.toString());

        Serializable eventObject = intent.getSerializableExtra(DataUtils.EXTRA_SELF);

        if (eventObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(eventObject instanceof Event)) {
            throw new ClassCastException("Intent's data object is not the expected type (Event)");
        }

        Event event = (Event) eventObject;
        IPersistable response = ApiManager.post(this, event);

        ResultReceiver resultReceiver = null;
        if (intent.hasExtra(DataUtils.EXTRA_RESULT_RECEIVER)) {
            resultReceiver = intent.getParcelableExtra(DataUtils.EXTRA_RESULT_RECEIVER);
        }

        if (resultReceiver != null) {
            if (response != null && response instanceof Event) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(DataUtils.EXTRA_RESULT, response);
                resultReceiver.send(Activity.RESULT_OK, bundle);
            } else {
                resultReceiver.send(Activity.RESULT_CANCELED, null);
            }
        }
    }
}
