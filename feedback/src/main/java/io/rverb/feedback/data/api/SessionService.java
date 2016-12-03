package io.rverb.feedback.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

import io.rverb.feedback.model.Session;

public class SessionService extends IntentService {
    public SessionService() {
        super("SessionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Serializable sessionObject = intent.getSerializableExtra("data");
        String tempFileName = intent.getStringExtra("temp_file_name");
        String apiKey = intent.getStringExtra("api_key");

        if (sessionObject == null) {
            throw new NullPointerException("Intent session object is null");
        }

        if (!(sessionObject instanceof Session)) {
            throw new ClassCastException("Intent session object is not the expected type (Session)");
        }

        Session session = (Session) sessionObject;
        postSession(apiKey, session, tempFileName);
    }

    void postSession(String apiKey, Session session, String tempFileName) {
        Map<String, String> params = new ArrayMap<>();

        params.put("SessionId", session.sessionId);
        params.put("SupportId", session.supportId);
        params.put("SessionStartUtc", session.sessionStartUtc);

        JSONObject paramJson = new JSONObject(params);

        ApiManager.post(apiKey, session.getTempFileNameTag(), paramJson, tempFileName);
    }
}
