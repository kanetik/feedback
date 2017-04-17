package io.rverb.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.rverb.feedback.model.DataItem;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.model.Session;
import io.rverb.feedback.presentation.RverbioFeedbackActivity;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.RverbioUtils;

@Keep
public class Rverbio {
    private static Context _appContext;
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
     * Helper method that indicates if Rverbio has been initialized
     *
     * @return boolean indicating that initialization has or has not occurred
     */
    public static boolean isInitialized() {
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
    public static void initialize(Context context) {
        initialize(context, new RverbioOptions());
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
    public static void initialize(Context context, RverbioOptions options) {
        _appContext = context.getApplicationContext();
        _options = options;
        _contextData = new ArrayList<>();

        // Send any previously queued requests
        RverbioUtils.sendQueuedRequests(_appContext);
        getInstance().setSessionData();
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
     * @param key The name of the context data item
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

        Feedback feedbackData = new Feedback(RverbioUtils.getApplicationId(_appContext),
                RverbioUtils.getSessionId(), endUser.endUserId, feedbackType,
                feedbackText, screenshotFileName);

        addSystemData(feedbackData);
        addInstanceContextDataToFeedback(feedbackData);

        RverbioUtils.recordData(_appContext, feedbackData);
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
        endUser.emailAddress = emailAddress;
        endUser.userIdentifier = userIdentifier;

        RverbioUtils.saveEndUser(_appContext, endUser);
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
        endUser.emailAddress = emailAddress;

        RverbioUtils.saveEndUser(_appContext, endUser);
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
        endUser.userIdentifier = userIdentifier;

        RverbioUtils.saveEndUser(_appContext, endUser);
    }

    /**
     * Show the feedback activity
     *
     * @param context The context from which you are launching the Feedback Activity.
     */
    public void startFeedbackActivity(@NonNull Context context) {
        File screenshot = null;
        if (context instanceof Activity && Rverbio._options.isAttachScreenshotEnabled()) {
            screenshot = RverbioUtils.takeScreenshot((Activity) context);
        }

        Intent feedbackIntent = new Intent(context, RverbioFeedbackActivity.class);
        if (screenshot != null) {
            feedbackIntent.putExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME, screenshot.getAbsolutePath());
        }

        context.startActivity(feedbackIntent);
    }

    private Rverbio setSessionData() {
        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        String sessionId = RverbioUtils.getSessionId();

        RverbioUtils.recordData(_appContext, new Session(sessionId, endUser.endUserId));

        return this;
    }

    private void addInstanceContextDataToFeedback(Feedback feedback) {
        if (_contextData != null) {
            feedback.contextData = _contextData;
        }
    }

    private void addSystemData(Feedback feedback) {
        feedback.appVersion = AppUtils.getVersionName(_appContext) + " (" + AppUtils.getVersionCode(_appContext) + ")";
        feedback.locale = Locale.getDefault().toString();
        feedback.deviceManufacturer = Build.MANUFACTURER;
        feedback.deviceModel = Build.MODEL;
        feedback.deviceName = Build.PRODUCT;
        feedback.osVersion = Build.VERSION.RELEASE;
        feedback.networkType = RverbioUtils.getNetworkType(_appContext);
    }
}
