package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.Serializable;

import io.rverb.feedback.model.Event;

public class EventService extends IntentService {
    public EventService() {
        super("EventService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Serializable eventObject = intent.getSerializableExtra("data");
        String tempFileName = intent.getStringExtra("temp_file_name");

        if (eventObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(eventObject instanceof Event)) {
            throw new ClassCastException("Intent's data object is not the expected type (Event)");
        }

        Event event = (Event) eventObject;
        ApiManager.post(this, tempFileName, event);
    }
}
