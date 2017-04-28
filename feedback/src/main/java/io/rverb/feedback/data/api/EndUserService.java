package io.rverb.feedback.data.api;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Patch;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.RverbioUtils;

public class EndUserService extends IntentService {
    public EndUserService() {
        super("EndUserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        ResultReceiver resultReceiver = null;
        if (intent.hasExtra(DataUtils.EXTRA_RESULT_RECEIVER)) {
            resultReceiver = intent.getParcelableExtra(DataUtils.EXTRA_RESULT_RECEIVER);
        }

        EndUser endUser = RverbioUtils.getEndUser(this);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            throw new IllegalStateException("EndUser must be initialized and contain an EndUserId");
        }

        boolean success;
        if (!endUser.isPersisted) {
            success = ApiManager.insertEndUser(this, endUser);
        } else {
            success = ApiManager.updateEndUser(this, endUser);
        }

        if (resultReceiver != null) {
            if (success) {
                resultReceiver.send(Activity.RESULT_OK, null);
            } else {
                resultReceiver.send(Activity.RESULT_CANCELED, null);
            }
        }
    }
}
