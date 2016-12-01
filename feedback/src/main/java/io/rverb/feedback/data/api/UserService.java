package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;

import java.io.Serializable;

import io.rverb.feedback.model.EndUser;

public class UserService extends IntentService {
    public UserService() {
        super("UserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable userObject = intent.getSerializableExtra("user_data");
        String tempFileName = intent.getStringExtra("temp_file_name");
        String apiKey = intent.getStringExtra("api_key");

        if (userObject == null) {
            throw new NullPointerException("Intent user object is null");
        }

        if (!(userObject instanceof EndUser)) {
            throw new ClassCastException("Intent user object is not the expected type (EndUser)");
        }

        EndUser endUser = (EndUser) userObject;

        ApiManager.postUser(apiKey, endUser, tempFileName);
    }
}
