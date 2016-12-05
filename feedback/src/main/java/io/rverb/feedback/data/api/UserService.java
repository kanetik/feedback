package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Patch;

public class UserService extends IntentService {
    public UserService() {
        super("UserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable userObject = intent.getSerializableExtra("data");
        String tempFileName = intent.getStringExtra("temp_file_name");

        if (userObject == null) {
            throw new NullPointerException("Intent EndUser object is null");
        }

        if (!(userObject instanceof EndUser)) {
            throw new ClassCastException("Intent user object is not the expected type (EndUser)");
        }

        EndUser endUser = (EndUser) userObject;

        if (RverbioUtils.isNullOrWhiteSpace(endUser.supportId)) {
            throw new IllegalStateException("Intent EndUser object must contain SupportId");
        }

        if (RverbioUtils.isNullOrWhiteSpace(endUser.emailAddress) && RverbioUtils.isNullOrWhiteSpace(endUser.userIdentifier)) {
            ApiManager.post(this, tempFileName, endUser);
        } else {
            patchUser(endUser, tempFileName);
        }
    }

    void patchUser(EndUser endUser, String tempFileName) {
        List<Patch> patches = new ArrayList<>();

        if (!RverbioUtils.isNullOrWhiteSpace(endUser.emailAddress)) {
            Patch patch = new Patch("replace", "/emailAddress", endUser.emailAddress);
            patches.add(patch);
        }

        if (!RverbioUtils.isNullOrWhiteSpace(endUser.userIdentifier)) {
            Patch patch = new Patch("replace", "/userIdentifier", endUser.userIdentifier);
            patches.add(patch);
        }

        ApiManager.patch(this, tempFileName, endUser.getDataTypeDescriptor(), patches, endUser.supportId);
    }
}
