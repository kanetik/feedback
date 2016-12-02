package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

import io.rverb.feedback.model.EndUser;

public class AddUserService extends IntentService {
    public AddUserService() {
        super("AddUserService");
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

        postUser(apiKey, (EndUser)userObject, tempFileName);
    }

    void postUser(String apiKey, EndUser endUser, String tempFileName) {
        Map<String, String> params = new ArrayMap<>();

        params.put("SupportId", endUser.supportId);
        params.put("UserIdentifier", "");
        params.put("EmailAddress", "");

        JSONObject paramJson = new JSONObject(params);

        ApiManager.post(apiKey, "enduser", paramJson, tempFileName);
    }
}
