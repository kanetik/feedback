package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import io.rverb.feedback.data.api.UserService;
import io.rverb.feedback.utility.RverbioUtils;

public class EndUser implements Cacheable {
    private static final long serialVersionUID = 354L;

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

        serviceIntent.putExtra("temp_file_name", cacheFileName);
        serviceIntent.putExtra("data", this);

        return serviceIntent;
    }
}
