package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;

import java.io.Serializable;

import io.rverb.feedback.model.SessionData;

public class SessionService extends IntentService {
    public SessionService() {
        super("SessionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable sessionObject = intent.getSerializableExtra("session_data");
        String tempFileName = intent.getStringExtra("temp_file_name");

        if (sessionObject == null) {
            throw new NullPointerException("Intent session object is null");
        }

        if (!(sessionObject instanceof SessionData)) {
            throw new ClassCastException("Intent session object is not the expected type (SessionData)");
        }

        SessionData session = (SessionData) sessionObject;
        ApiManager.postSession(session, tempFileName);
    }
}
