package com.kanetik.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.kanetik.feedback.model.DataItem;
import com.kanetik.feedback.model.Feedback;
import com.kanetik.feedback.presentation.FeedbackActivity;
import com.kanetik.feedback.utility.AppUtils;
import com.kanetik.feedback.utility.FeedbackUtils;
import com.kanetik.feedback.utility.LogUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

@Keep
public class KanetikFeedback {
    static Context _appContext;

    private static boolean _initialized = false;

    private static String _apiKey;
    private static boolean _debug;

    private static ArrayList<DataItem> _contextData;

    private static KanetikFeedback _instance;

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
     * Gets the ApiKey for this KanetikFeedback instance.
     *
     * @return API Key
     */
    public static String getApiKey() {
        return _apiKey;
    }

    public static void initialize(Context context, String apiKey) {
        initialize(context, apiKey, false);
    }

    /**
     * Initializes the KanetikFeedback singleton. The developer's interactions with Kanetik KanetikFeedback will be
     * entirely via the singleton.
     * <p>
     * Initialization must be done before the KanetikFeedback singleton can be used.
     *
     * @param context Activity or Application Context
     */
    public static void initialize(Context context, String apiKey, boolean debug) {
        if (isInitialized()) {
            return;
        }

        new KanetikFeedback(context);

        _apiKey = apiKey;
        _debug = debug;

        _contextData = new ArrayList<>();

        if (_debug) {
            LogUtils.i("KanetikFeedback Initialize");
        }

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
        DataItem newItem = new DataItem(key, value);

        if (_contextData.contains(newItem)) {
            _contextData.remove(newItem);
        }

        _contextData.add(newItem);
        return _instance;
    }

    /**
     * Add a collection of name-value pairs to be sent to the developer with a feedback request.
     *
     * @param items The map of name-value pairs to be sent
     */
    public KanetikFeedback addContextDataItems(Map<String, Object> items) {
        for (Map.Entry<String, Object> item : items.entrySet()) {
            DataItem newItem = new DataItem(item.getKey(), item.getValue());

            if (_contextData.contains(newItem)) {
                _contextData.remove(newItem);
            }

            _contextData.add(newItem);
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
        for (DataItem item : _contextData) {
            if (item.equals(key)) {
                _contextData.remove(item);
            }
        }

        return _instance;
    }

    public ArrayList<DataItem> getContextData() {
        return _contextData;
    }

    /**
     * Sends the user's request to the developer.
     *
     * @param feedbackType The type of feedback submitted by the user.
     * @param feedbackText The text submitted by the end-user.
     */
    public void sendFeedback(String feedbackType, String feedbackText) {
        final Feedback feedbackData =
                new Feedback(feedbackText);

        addSystemData(feedbackData);
        addInstanceContextDataToFeedback(feedbackData);

        FeedbackUtils.persistData(_appContext, feedbackData, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    AppUtils.alertUser(_appContext);
                } else {
                    FeedbackUtils.handlePersistenceFailure(_appContext, feedbackData);
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

    private void addSystemData(Feedback feedback) {
        feedback.appVersion = AppUtils.getVersionName(_appContext) + " (" + AppUtils.getVersionCode(_appContext) + ")";
        feedback.locale = Locale.getDefault().toString();
        feedback.deviceManufacturer = Build.MANUFACTURER;
        feedback.deviceModel = Build.MODEL;
        feedback.deviceName = Build.PRODUCT;
        feedback.osVersion = Build.VERSION.RELEASE;
        feedback.networkType = AppUtils.getNetworkType(_appContext);
    }

    private void addInstanceContextDataToFeedback(Feedback feedback) {
        if (_contextData != null) {
            feedback.contextData = _contextData;
        }
    }
}
