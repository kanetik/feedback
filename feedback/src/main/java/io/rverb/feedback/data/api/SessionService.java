package io.rverb.feedback.data.api;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.Serializable;

import io.rverb.feedback.model.Persistable;
import io.rverb.feedback.model.Session;
import io.rverb.feedback.utility.DataUtils;

public class SessionService extends IntentService {
    public SessionService() {
        super("SessionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Serializable sessionObject = intent.getSerializableExtra(DataUtils.EXTRA_SELF);

        if (sessionObject == null) {
            throw new NullPointerException("Intent's data object is null");
        }

        if (!(sessionObject instanceof Session)) {
            throw new ClassCastException("Intent's data object is not the expected type (Session)");
        }

        Session session = (Session) sessionObject;
        Persistable response = ApiManager.post(this, session);

        ResultReceiver resultReceiver = null;
        if (intent.hasExtra(DataUtils.EXTRA_RESULT_RECEIVER)) {
            resultReceiver = intent.getParcelableExtra(DataUtils.EXTRA_RESULT_RECEIVER);
        }

        if (resultReceiver != null) {
            if (response != null && response instanceof Session) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(DataUtils.EXTRA_RESULT, response);
                resultReceiver.send(Activity.RESULT_OK, bundle);
            } else {
                resultReceiver.send(Activity.RESULT_CANCELED, null);
            }
        }
    }
}
