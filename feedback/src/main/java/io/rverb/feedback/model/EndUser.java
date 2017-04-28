package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import java.util.UUID;

import io.rverb.feedback.data.api.EndUserService;
import io.rverb.feedback.utility.DataUtils;

public class EndUser {
    public String endUserId;
    public String emailAddress;
    public String userIdentifier;
    public boolean isPersisted;

    public EndUser() {
        this.endUserId = UUID.randomUUID().toString();
        this.emailAddress = "";
        this.userIdentifier = "";
        this.isPersisted = false;
    }

    public Intent getPersistServiceIntent(Context context, ResultReceiver resultReceiver) {
        Intent serviceIntent = new Intent(context, EndUserService.class);

        serviceIntent.putExtra(DataUtils.EXTRA_RESULT_RECEIVER, resultReceiver);

        return serviceIntent;
    }

    @Override
    public String toString() {
        return "endUserId: " + endUserId + " | emailAddress: " + emailAddress + " | userIdentifier: " + userIdentifier;
    }
}
