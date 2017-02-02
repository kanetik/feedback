package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import io.rverb.feedback.data.api.UserService;
import io.rverb.feedback.utility.RverbioUtils;

public class EndUser implements Cacheable {
    private static final long serialVersionUID = 354L;

    public String supportId;
    public String emailAddress;
    public String userIdentifier;

    public EndUser(Context context, String supportId) {
        this.supportId = supportId;
        this.emailAddress = "";
        this.userIdentifier = "";

        RverbioUtils.saveEndUserEmailAddress(context, emailAddress);
    }

    public void setEmailAddress(Context context, String emailAddress) {
        this.emailAddress = emailAddress;

        RverbioUtils.saveEndUserEmailAddress(context, emailAddress);
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    @Override
    public String toString() {
        return "supportId: " + supportId + " | emailAddress: " + emailAddress
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
