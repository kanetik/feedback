package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;

import java.io.Serializable;

import io.rverb.feedback.model.Session;

public class SessionService extends IntentService {
    public SessionService() {
        super("SessionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable sessionObject = intent.getSerializableExtra("data");
        String tempFileName = intent.getStringExtra("temp_file_name");

        if (sessionObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(sessionObject instanceof Session)) {
            throw new ClassCastException("Intent's data object is not the expected type (Session)");
        }

        Session session = (Session) sessionObject;
        ApiManager.post(this, tempFileName, session);
    }
}
