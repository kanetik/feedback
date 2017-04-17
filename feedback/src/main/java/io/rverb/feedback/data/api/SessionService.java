package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;

import java.io.Serializable;

import io.rverb.feedback.model.Cacheable;
import io.rverb.feedback.model.Session;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.RverbioUtils;

public class SessionService extends IntentService {
    public SessionService() {
        super("SessionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Serializable sessionObject = intent.getSerializableExtra(DataUtils.EXTRA_DATA);
        String tempFileName = intent.getStringExtra(DataUtils.EXTRA_TEMPORARY_FILE_NAME);

        if (sessionObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(sessionObject instanceof Session)) {
            throw new ClassCastException("Intent's data object is not the expected type (Session)");
        }

        Session session = (Session) sessionObject;

        Cacheable response = ApiManager.postWithResponse(this, tempFileName, session);
        if (response != null && response instanceof Session) {
            Session responseSession = (Session) response;
            RverbioUtils.saveApplicationId(this, responseSession.applicationId);
        }
    }
}
