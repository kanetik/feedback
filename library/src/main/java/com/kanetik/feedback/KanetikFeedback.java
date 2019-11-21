package com.kanetik.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.kanetik.feedback.model.ContextDataItem;
import com.kanetik.feedback.model.Feedback;
import com.kanetik.feedback.presentation.FeedbackActivity;
import com.kanetik.feedback.utility.FeedbackUtils;
import com.kanetik.feedback.utility.LogUtils;

import java.util.ArrayList;
import java.util.Map;

@Keep
public class KanetikFeedback {
    private static Context _appContext;

    private static boolean _initialized = false;

    private static boolean _debug;
    private static String _userIdentifier;

    private static ArrayList<ContextDataItem> _contextData;

    private static KanetikFeedback _instance;

    public ArrayList<ContextDataItem> getContextData() {
        if (_contextData == null) {
            _contextData = new ArrayList<>();
        }

        return _contextData;
    }

    public KanetikFeedback(@NonNull Context context) {
        _appContext = context;
    }

    /**
     * Gets the Kanetik feedback singleton, which is the primary interaction point the developer will have
     * with the feedback SDK.
     *
     * @return KanetikFeedback singleton instance.
     */
    public static KanetikFeedback getInstance(@NonNull Context context) {
        synchronized (KanetikFeedback.class) {
            if (_instance == null || _appContext == null) {
                _instance = new KanetikFeedback(context.getApplicationContext());
            }

            return _instance;
        }
    }

    /**
     * Helper method that indicates if KanetikFeedback has been initialized and is ready to use
     *
     * @return boolean indicating that initialization has or has not completed
     */
    public static boolean isInitialized() {
        return _initialized;
    }

    /**
     * Gets the Debugging state.
     *
     * @return debugging
     */
    public static boolean isDebug() {
        return _debug;
    }

    /**
     * Gets the User Identifier.
     *
     * @return userIdentifier
     */
    public static String getUserIdentifier() {
        return _userIdentifier;
    }

    /**
     * Initializes the KanetikFeedback singleton. The developer's interactions with Kanetik KanetikFeedback will be
     * entirely via the singleton.
     * <p>
     * Initialization must be done before the KanetikFeedback singleton can be used.
     *
     * @param context Activity or Application Context
     */
    public static void initialize(final Context context, String userIdentifier, boolean debug) {
        if (isInitialized()) {
            return;
        }

        new KanetikFeedback(context);

        _debug = debug;
        if (_debug) {
            LogUtils.i("KanetikFeedback Initialize");
        }

        _userIdentifier = userIdentifier;

        // Send any previously queued requests
        FeedbackUtils.sendQueuedRequests(context);

        _initialized = true;
    }

    /**
     * Add a single name-value pair to be sent to the developer with a feedback request.
     *
     * @param key   The name of the context data item
     * @param value The value of the context data item
     */
    public KanetikFeedback addContextDataItem(String key, String value) {
        ContextDataItem newItem = new ContextDataItem(key, value);

        getContextData().remove(newItem);
        getContextData().add(newItem);

        return _instance;
    }

    /**
     * Add a collection of name-value pairs to be sent to the developer with a feedback request.
     *
     * @param items The map of name-value pairs to be sent
     */
    public KanetikFeedback addContextDataItems(Map<String, Object> items) {
        for (Map.Entry<String, Object> item : items.entrySet()) {
            ContextDataItem newItem = new ContextDataItem(item.getKey(), item.getValue());

            getContextData().remove(newItem);
            getContextData().add(newItem);
        }

        return _instance;
    }

    /**
     * Remove a single name-value pair from the context data to be sent to the developer with
     * a feedback request.
     *
     * @param key The name of the context data item to be removed
     */
    public KanetikFeedback removeContextDataItem(String key) {
        for (ContextDataItem item : _contextData) {
            if (item.key.equals(key)) {
                _contextData.remove(item);
            }
        }

        return _instance;
    }

    /**
     * Sends the user's request to the developer.
     *
     * @param feedbackText The text submitted by the end-user.
     */
    public void sendFeedback(String feedbackText, String from) {
        final Feedback feedback = new Feedback(_appContext, feedbackText, from);

        FeedbackUtils.addInstanceContextDataToFeedback(_appContext, feedback);

        FeedbackUtils.persistData(_appContext, feedback, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    FeedbackUtils.alertUser(_appContext);
                } else {
                    FeedbackUtils.handlePersistenceFailure(_appContext, feedback);
                }
            }
        });
    }

    /**
     * Show the feedback activity
     *
     * @param context The context from which you are launching the KanetikFeedback Activity.
     */
    public void startFeedbackActivity(@NonNull Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }
}
