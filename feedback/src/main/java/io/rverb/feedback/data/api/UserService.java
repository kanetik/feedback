package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import io.rverb.feedback.model.EndUser;

public class UserService extends IntentService {
    private final static String userEmailJsonFormat = "[{\"op\":\"replace\",\"path\":\"/emailAddress\",\"value\":\"%s\"}]";
    private final static String userIdentifierJsonFormat = "[{\"op\":\"replace\",\"path\":\"/userIdentifier\",\"value\":\"%s\"}]";

    public UserService() {
        super("UserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable userObject = intent.getSerializableExtra("data");
        String tempFileName = intent.getStringExtra("temp_file_name");
        String apiKey = intent.getStringExtra("api_key");

        if (userObject == null) {
            throw new NullPointerException("Intent EndUser object is null");
        }

        if (!(userObject instanceof EndUser)) {
            throw new ClassCastException("Intent user object is not the expected type (EndUser)");
        }

        EndUser endUser = (EndUser) userObject;

        if (TextUtils.isEmpty(endUser.supportId)) {
            throw new IllegalStateException("Intent EndUser object must contain SupportId");
        }

        if (TextUtils.isEmpty(endUser.emailAddress) && TextUtils.isEmpty(endUser.userIdentifier)) {
            postUser(apiKey, endUser, tempFileName);
        } else {
            patchUser(apiKey, endUser, tempFileName);
        }
    }

    void postUser(String apiKey, EndUser endUser, String tempFileName) {
        Map<String, String> params = new ArrayMap<>();

        params.put("SupportId", endUser.supportId);
        params.put("UserIdentifier", "");
        params.put("EmailAddress", "");

        JSONObject paramJson = new JSONObject(params);

        ApiManager.post(apiKey, endUser.getTempFileNameTag(), paramJson, tempFileName);
    }

    void patchUser(String apiKey, EndUser endUser, String tempFileName) {
        String jsonString = "";

        if (!TextUtils.isEmpty(endUser.emailAddress)) {
            jsonString = String.format(Locale.US, userEmailJsonFormat, endUser.emailAddress);

            try {
                JSONArray json = new JSONArray(jsonString);
                ApiManager.patch(apiKey, endUser.getTempFileNameTag(), endUser.supportId, json, tempFileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(endUser.userIdentifier)) {
            jsonString = String.format(Locale.US, userIdentifierJsonFormat, endUser.userIdentifier);

            try {
                JSONArray json = new JSONArray(jsonString);
                ApiManager.patch(apiKey, endUser.getTempFileNameTag(), endUser.supportId, json, tempFileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
