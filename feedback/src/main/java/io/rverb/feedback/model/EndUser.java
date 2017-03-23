package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import io.rverb.feedback.data.api.UserService;
import io.rverb.feedback.utility.DataUtils;

public class EndUser implements Cacheable {
    static final long serialVersionUID = 354L;

    public String endUserId;
    public String emailAddress;
    public String userIdentifier;

    public EndUser() {
        this.endUserId = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "endUserId: " + endUserId + " | emailAddress: " + emailAddress
                + " | userIdentifier: " + userIdentifier;
    }

    @Override
    public String getDataTypeDescriptor() {
        return "enduser";
    }

    @Override
    public Intent getServiceIntent(Context context, String cacheFileName) {
        Intent serviceIntent = new Intent(context, UserService.class);

        serviceIntent.putExtra(DataUtils.EXTRA_TEMPORARY_FILE_NAME, cacheFileName);
        serviceIntent.putExtra(DataUtils.EXTRA_DATA, this);

        return serviceIntent;
    }
}
