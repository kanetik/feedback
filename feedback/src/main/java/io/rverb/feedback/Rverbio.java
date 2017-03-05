package io.rverb.feedback;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.model.Session;
import io.rverb.feedback.presentation.RverbioFeedbackDialogFragment;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.RverbioUtils;

@Keep
public class Rverbio {
    private static Context _appContext;
    private static HashMap<String, String> _contextData;
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
     * Initializes the Rverbio singleton. The developer's interactions with rverb.io will be
     * entirely via the singleton.
     *
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
     *
     * Initialization must be done before the Rverbio singleton can be used.
     *
     * @param context Activity or Application Context
     * @param options RverbioOptions object to set defaults
     */
    public static void initialize(Context context, RverbioOptions options) {
        _appContext = context.getApplicationContext();
        _options = options;
        _contextData = new HashMap<>();

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
     * Gets the collection of Key/Value Pairs that will be sent with a feedback request
     * You can add and remove items as you would to any map.
     *
     * @return HashMap of context data items
     */
    public HashMap<String, String> getContextData() {
        return _contextData;
    }

    /**
     * Sends the user's request to the developer.
     *
     * @param feedbackType   The type of feedback submitted by the user.
     * @param feedbackText   The text submitted by the end-user.
     * @param screenshot The screenshot of the app visible on the user's screen.
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
        addContextData(feedbackData);

        RverbioUtils.recordData(_appContext, feedbackData);
    }

    /**
     * Updates the user's email address and userIdentifier. Both fields will be overwritten by the
     * data provided, including empty strings. If you only want to update one field, use the
     * appropriate update method.
     *
     * @param emailAddress The end-user's contact email address.
     * @param userIdentifier A string that the developer knows the user by; for instance, a
     *                       useraccount number. This should never include private information like
     *                       credit card numbers or phone numbers.
     *
     * @see Rverbio#setUserEmail( String)
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
     *
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
     *
     * @see Rverbio#setUserInfo(String, String)
     * @see Rverbio#setUserEmail(String)
     */
    public void setUserIdentifier(@NonNull String userIdentifier) {
        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        endUser.userIdentifier = userIdentifier;

        RverbioUtils.saveEndUser(_appContext, endUser);
    }

    /**
     * Show the feedback dialog
     *
     * @param activity The activity on which you wish to show the feedback dialog. This activity
     *                 must be a subclass of Activity.
     */
    public void showDialog(@NonNull Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_edit_name");

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        final RverbioFeedbackDialogFragment fragment = RverbioFeedbackDialogFragment.create();
        fragment.show(manager, "fragment_edit_name");
    }

//    /**
//     * Takes a screenshot of the app as currently visible on the user's device. This
//     * will give the developer context around the comments or questions submitted by the user.
//     *
//     * @param activity The activity from which you wish to take a screenshot.
//     */
//    public File takeScreenshot(@NonNull Activity activity) {
//        File screenshot = RverbioUtils.createScreenshotFile(activity);
//        if (screenshot != null) {
//            LogUtils.d("Screenshot File", screenshot.getAbsolutePath());
//
//            Uri path = Uri.fromFile(screenshot);
//            if (path != null) {
//                return screenshot;
//            }
//
//            // TODO: The image uploadUrl expires after 30 minutes. If the image hasn't been uploaded
//            // by then, delete the local copy. Make the timeout configurable.
//        }
//
//        return null;
//    }

//    /**
//     * Logs events related to feedback, such as feedback request started, session started, etc.
//     *
//     * @param event The event to track.
//     */
//    public void sendEvent(String event) {
//        EndUser endUser = RverbioUtils.getEndUser(_appContext);
//        Event eventData = new Event(endUser.endUserId, event);
//        RverbioUtils.recordData(_appContext, eventData);
//    }

    private Rverbio setSessionData() {
        EndUser endUser = RverbioUtils.getEndUser(_appContext);
        String sessionId = RverbioUtils.getSessionId();

        RverbioUtils.recordData(_appContext, new Session(sessionId, endUser.endUserId));

        return this;
    }

    private void addContextData(Feedback feedback) {
        Map<String, String> contextData = Rverbio.getInstance().getContextData();

        if (contextData != null && contextData.size() > 0) {
            feedback.contextData = contextData;
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
