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
    private static Context _appContext;
    private static String _apiKey;
    private static ArrayList<DataItem> _contextData;
    private static RverbioOptions _options;

    private static final Rverbio _instance = new Rverbio();

    /**
     * Gets the rverb.io singleton, which is the primary interaction point the developer will have
     * with the rverb.io SDK.
     *
     * @return Rverbio singleton instance.
     */
    public static Rverbio getInstance() {
        if (_appContext == null) {
            throw new IllegalStateException("You must call Rverbio#initialize before accessing the Rverbio instance",
                    new Throwable("Rverbio instance not initialized"));
        }

        return _instance;
    }

    /**
     * Helper method that indicates if Rverbio has been initialized and is ready to use
     *
     * @return boolean indicating that initialization has or has not completed
     */
    public static boolean isReady() {
        return _appContext != null;
    }

    /**
     * Initializes the Rverbio singleton. The developer's interactions with rverb.io will be
     * entirely via the singleton.
     * <p>
     * Initialization must be done before the Rverbio singleton can be used.
     *
     * @param context Activity or Application Context
     */
    public static void initialize(Context context, String apiKey) {
        initialize(context, apiKey, new RverbioOptions());
    }

    /**
     * Initializes the Rverbio singleton, with options. The developer's interactions with
     * rverb.io will be entirely via the singleton.
     * <p>
     * Initialization must be done before the Rverbio singleton can be used.
     *
     * @param context Activity or Application Context
     * @param options RverbioOptions object to set defaults
     */
    public static void initialize(Context context, String apiKey, RverbioOptions options) {
        if (isReady()) {
            return;
        }

        _appContext = context.getApplicationContext();
        _apiKey = apiKey;
        _options = options;
        _contextData = new ArrayList<>();

        if (Rverbio.getInstance().getOptions().isDebugMode()) {
            LogUtils.i("Rverbio Initialize");
        }

        RverbioUtils.setSessionStart(context);

        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            // Create the EndUser object for future feedback and events
            RverbioUtils.setEndUser(_appContext, new EndUser());
        } else {
            // Send any previously queued requests
            boolean sentQueuedFeedback = RverbioUtils.sendQueuedRequests(_appContext);
            if (sentQueuedFeedback && (!endUser.isPersisted || !endUser.isSynced)) {
                RverbioUtils.persistEndUser(_appContext);
            }
        }
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
     * Gets the ApiKey for this Rverbio instance.
     *
     * @return API Key for the current instance
     */
    public String getApiKey() {
        return _apiKey;
    }

    /**
     * Add a single name-value pair to be sent to Rverb.io with a feedback request.
     *
     * @param key   The name of the context data item
     * @param value The value of the context data item
     */
    public void addContextDataItem(String key, String value) {
        _contextData.add(new DataItem(key, value));
    }

    /**
     * Add a collection of name-value pairs to be sent to Rverb.io with a feedback request.
     *
     * @param items The map of name-value pairs to be sent
     */
    public void addContextDataItems(Map<String, Object> items) {
        for (Map.Entry<String, Object> item : items.entrySet()) {
            _contextData.add(new DataItem(item.getKey(), item.getValue()));
        }
    }

    /**
     * Remove a single name-value pair from the context data to be sent to Rverb.io with
     * a feedback request.
     *
     * @param key The name of the context data item to be removed
     */
    public void removeContextDataItem(String key) {
        _contextData.remove(key);
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
                        RverbioUtils.notifyUser(_appContext, RverbioUtils.FEEDBACK_SUBMITTED);
                    } else {
                        RverbioUtils.notifyUser(_appContext, RverbioUtils.ANONYMOUS_FEEDBACK_SUBMITTED);
                    }
                } else {
                    RverbioUtils.handlePersistanceFailure(_appContext, feedbackData);
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
    public void setUserIdentifier(@NonNull String userIdentifier) {
        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            throw new IllegalStateException("You must call Rverbio#initialize to initialize the EndUser",
                    new Throwable("Rverbio instance not initialized"));
        }

        if (endUser.userIdentifier.equalsIgnoreCase(userIdentifier)) {
            return;
        }

        endUser.userIdentifier = userIdentifier;
        endUser.isSynced = false;

        RverbioUtils.setEndUser(_appContext, endUser);
    }

    /**
     * Show the feedback activity
     *
     * @param context The context from which you are launching the Feedback Activity.
     */
    public void startFeedbackActivity(@NonNull Context context) {
        File screenshot = null;
        if (context instanceof Activity && Rverbio._options.attachScreenshotByDefault()) {
            screenshot = RverbioUtils.takeScreenshot((Activity) context);
        }

        Intent feedbackIntent = new Intent(context, RverbioFeedbackActivity.class);
        if (screenshot != null) {
            feedbackIntent.putExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME, screenshot.getAbsolutePath());
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
