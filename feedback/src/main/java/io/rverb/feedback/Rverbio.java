package io.rverb.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import io.rverb.feedback.model.DataItem;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.presentation.RverbioFeedbackActivity;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.LogUtils;
import io.rverb.feedback.utility.RverbioUtils;

@Keep
public class Rverbio {
    static Context _appContext;

    private static boolean _initialized = false;

    private static String _apiKey;
    private static boolean _debug;

    private static ArrayList<DataItem> _contextData;
    private static RverbioOptions _options;

    private static Rverbio _instance;

    public Rverbio(@NonNull Context context) {
        _appContext = context;
    }

    /**
     * Gets the rverb.io singleton, which is the primary interaction point the developer will have
     * with the rverb.io SDK.
     *
     * @return Rverbio singleton instance.
     */
    public static Rverbio getInstance(@NonNull Context context) {
        synchronized (Rverbio.class) {
            if (_instance == null || _appContext == null) {
                _instance = new Rverbio(context.getApplicationContext());
            }

            return _instance;
        }
    }

    /**
     * Helper method that indicates if Rverbio has been initialized and is ready to use
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
     * Gets the ApiKey for this Rverbio instance.
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
     * Initializes the Rverbio singleton. The developer's interactions with rverb.io will be
     * entirely via the singleton.
     * <p>
     * Initialization must be done before the Rverbio singleton can be used.
     *
     * @param context Activity or Application Context
     */
    public static void initialize(Context context, String apiKey, boolean debug) {
        if (isInitialized()) {
            return;
        }

        new Rverbio(context);

        _apiKey = apiKey;
        _debug = debug;

        _options = new RverbioOptions();
        _contextData = new ArrayList<>();

        if (_debug) {
            LogUtils.i("Rverbio Initialize");
        }

        RverbioUtils.setSessionStart(context);

        EndUser endUser = RverbioUtils.getEndUser(context);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            // Create the EndUser object for future feedback and events
            RverbioUtils.setEndUser(context, new EndUser());
        } else {
            // Send any previously queued requests
            boolean sentQueuedFeedback = RverbioUtils.sendQueuedRequests(context);
            if (sentQueuedFeedback && (!endUser.isPersisted || !endUser.isSynced)) {
                RverbioUtils.persistEndUser(context);
            }
        }

        _initialized = true;
    }

    /**
     * Gets the options object for this Rverbio instance. You can make changes to the default
     * settings through this object. At this time, the only thing that can be set is "take screenshot".
     *
     * @return RverbioOptions for the current instance
     */
    public RverbioOptions getOptions() {
        return _options;
    }

    /**
     * Add a single name-value pair to be sent to Rverb.io with a feedback request.
     *
     * @param key   The name of the context data item
     * @param value The value of the context data item
     */
    public Rverbio addContextDataItem(String key, String value) {
        DataItem newItem = new DataItem(key, value);

        if (_contextData.contains(newItem)) {
            _contextData.remove(newItem);
        }

        _contextData.add(newItem);
        return _instance;
    }

    /**
     * Add a collection of name-value pairs to be sent to Rverb.io with a feedback request.
     *
     * @param items The map of name-value pairs to be sent
     */
    public Rverbio addContextDataItems(Map<String, Object> items) {
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
     * Remove a single name-value pair from the context data to be sent to Rverb.io with
     * a feedback request.
     *
     * @param key The name of the context data item to be removed
     */
    public Rverbio removeContextDataItem(String key) {
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
     * @param screenshot   The screenshot of the app visible on the user's screen.
     */
    public void sendFeedback(String feedbackType, String feedbackText, File screenshot) {
        String screenshotFileName = "";
        if (screenshot != null) {
            screenshotFileName = screenshot.getAbsolutePath();
        }

        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            throw new IllegalStateException("You must call Rverbio#initialize to initialize the EndUser",
                    new Throwable("Rverbio instance not initialized"));
        } else {
            RverbioUtils.persistEndUser(_appContext);
        }

        final Feedback feedbackData =
                new Feedback(endUser.endUserId,
                        feedbackType,
                        feedbackText,
                        screenshotFileName);

        addSystemData(feedbackData);
        addSessionStartData(feedbackData);
        addInstanceContextDataToFeedback(feedbackData);

        RverbioUtils.persistData(_appContext, feedbackData, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    EndUser endUser = RverbioUtils.getEndUser(_appContext);
                    if (endUser != null && !RverbioUtils.isNullOrWhiteSpace(endUser.emailAddress)) {
                        RverbioUtils.alertUser(_appContext, RverbioUtils.FEEDBACK_SUBMITTED);
                    } else {
                        RverbioUtils.alertUser(_appContext, RverbioUtils.ANONYMOUS_FEEDBACK_SUBMITTED);
                    }
                } else {
                    RverbioUtils.handlePersistenceFailure(_appContext, feedbackData);
                }
            }
        });
    }

    /**
     * Updates the user's email address and userIdentifier. Both fields will be overwritten by the
     * data provided, including empty strings. If you only want to update one field, use the
     * appropriate update method.
     *
     * @param emailAddress   The end-user's contact email address.
     * @param userIdentifier A string that the developer knows the user by; for instance, a
     *                       useraccount number. This should never include private information like
     *                       credit card numbers or phone numbers.
     * @see Rverbio#setUserEmail(String)
     * @see Rverbio#setUserIdentifier(String)
     */
    public void setUserInfo(@NonNull String emailAddress, @NonNull String userIdentifier) {
        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            throw new IllegalStateException("You must call Rverbio#initialize to initialize the EndUser",
                    new Throwable("Rverbio instance not initialized"));
        }

        if (endUser.emailAddress.equalsIgnoreCase(emailAddress) && endUser.userIdentifier.equalsIgnoreCase(userIdentifier)) {
            return;
        }

        endUser.emailAddress = emailAddress;
        endUser.userIdentifier = userIdentifier;
        endUser.isSynced = false;

        RverbioUtils.setEndUser(_appContext, endUser);
    }

    /**
     * Updates the user's email address.
     *
     * @param emailAddress The end-user's contact email address.
     * @see Rverbio#setUserInfo(String, String)
     * @see Rverbio#setUserIdentifier(String)
     */
    public void setUserEmail(@NonNull String emailAddress) {
        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            throw new IllegalStateException("You must call Rverbio#initialize to initialize the EndUser",
                    new Throwable("Rverbio instance not initialized"));
        }

        if (endUser.emailAddress.equalsIgnoreCase(emailAddress)) {
            return;
        }

        endUser.emailAddress = emailAddress;
        endUser.isSynced = false;

        RverbioUtils.setEndUser(_appContext, endUser);
    }

    /**
     * Updates the user's userIdentifier.
     *
     * @param userIdentifier A string that the developer knows the user by; for instance, a
     *                       useraccount number. This should never include private information like
     *                       credit card numbers or phone numbers.
     * @see Rverbio#setUserInfo(String, String)
     * @see Rverbio#setUserEmail(String)
     */
    public Rverbio setUserIdentifier(@NonNull String userIdentifier) {
        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            throw new IllegalStateException("You must call Rverbio#initialize to initialize the EndUser",
                    new Throwable("Rverbio instance not initialized"));
        }

        if (endUser.userIdentifier.equalsIgnoreCase(userIdentifier)) {
            return _instance;
        }

        endUser.userIdentifier = userIdentifier;
        endUser.isSynced = false;

        RverbioUtils.setEndUser(_appContext, endUser);

        return _instance;
    }

    public Rverbio setAttachScreenshotEnabled(boolean attachScreenshot) {
        _options.setAttachScreenshotEnabled(attachScreenshot);
        return _instance;
    }

    public Rverbio setUseNotifications(boolean useNotifications) throws IllegalStateException {
        if (useNotifications) {
            // If we're disabling the use of notifications, notification channel is not required.
            _options.setNotificationChannel();
        }

        _options.setUseNotifications(useNotifications);
        return _instance;
    }

    /**
     * Show the feedback activity
     *
     * @param context The context from which you are launching the Feedback Activity.
     */
    public void startFeedbackActivity(@NonNull Context context) {
        Intent feedbackIntent = new Intent(context, RverbioFeedbackActivity.class);

        if (context instanceof Activity && Rverbio._options.attachScreenshotByDefault()) {
            File screenshot = RverbioUtils.takeScreenshot((Activity) context);
            if (screenshot != null) {
                feedbackIntent.putExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME, screenshot.getAbsolutePath());
            }
        }

        context.startActivity(feedbackIntent);
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

    private void addSessionStartData(Feedback feedback) {
        feedback.sessionStartsUtc = RverbioUtils.getSessionStartTimestamps(_appContext);
    }

    private void addInstanceContextDataToFeedback(Feedback feedback) {
        if (_contextData != null) {
            feedback.contextData = _contextData;
        }
    }
}
