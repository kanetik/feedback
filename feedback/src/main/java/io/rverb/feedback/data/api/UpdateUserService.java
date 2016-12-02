package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.Locale;

import io.rverb.feedback.model.EndUser;

public class UpdateUserService extends IntentService {
    private final static String userEmailJsonFormat = "[{\"op\":\"replace\",\"path\":\"/emailAddress\",\"value\":\"%s\"}]";
    private final static String userIdentifierJsonFormat = "[{\"op\":\"replace\",\"path\":\"/userIdentifier\",\"value\":\"%s\"}]";

    public UpdateUserService() {
        super("UpdateUserService");
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

        if (TextUtils.isEmpty(endUser.supportId)) {
            throw new IllegalStateException("Intent user object must contain SupportId");
        }

        if (TextUtils.isEmpty(endUser.emailAddress) && TextUtils.isEmpty(endUser.userIdentifier)) {
            throw new IllegalStateException("Intent user object must contain an emailAddress or userIdentifier");
        }

        patchUser(apiKey, endUser, tempFileName);
    }

    void patchUser(String apiKey, EndUser endUser, String tempFileName) {
        String jsonString = "";

        if (!TextUtils.isEmpty(endUser.emailAddress)) {
            jsonString = String.format(Locale.US, userEmailJsonFormat, endUser.emailAddress);

            try {
                JSONArray json = new JSONArray(jsonString);
                ApiManager.patch(apiKey, "enduser", endUser.supportId, json, tempFileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(endUser.userIdentifier)) {
            jsonString = String.format(Locale.US, userIdentifierJsonFormat, endUser.userIdentifier);

            try {
                JSONArray json = new JSONArray(jsonString);
                ApiManager.patch(apiKey, "enduser", endUser.supportId, json, tempFileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
