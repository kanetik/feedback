package io.rverb.feedback;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

import io.rverb.feedback.model.Cacheable;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.model.Session;
import io.rverb.feedback.presentation.RverbioFeedbackDialogFragment;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.LogUtils;
import io.rverb.feedback.utility.RverbioUtils;

import static io.rverb.feedback.utility.RverbioUtils.getSupportId;

public class Rverbio {
    private static Context _appContext;

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
        _appContext = context.getApplicationContext();

        // Send any previously queued requests
        getInstance().sendQueuedRequests();
        getInstance().initEndUser().setSessionData();
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

        Feedback feedbackData = new Feedback(RverbioUtils.getSupportId(_appContext), feedbackType,
                feedbackText, screenshotFileName);

        recordData(feedbackData);
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
     * @see Rverbio#updateUserEmail(String)
     * @see Rverbio#updateUserIdentifier(String)
     */
    public void updateUserInfo(String emailAddress, String userIdentifier) {
        EndUser endUser = new EndUser(RverbioUtils.getSupportId(_appContext));
        endUser.setEmailAddress(emailAddress);
        endUser.setUserIdentifier(userIdentifier);

        recordData(endUser);
    }

    /**
     * Updates the user's email address.
     *
     * @param emailAddress The end-user's contact email address.
     *
     * @see Rverbio#updateUserInfo(String, String)
     * @see Rverbio#updateUserIdentifier(String)
     */
    public void updateUserEmail(String emailAddress) {
        EndUser endUser = new EndUser(RverbioUtils.getSupportId(_appContext));
        endUser.setEmailAddress(emailAddress);

        recordData(endUser);
    }

    /**
     * Updates the user's userIdentifier.
     *
     * @param userIdentifier A string that the developer knows the user by; for instance, a
     *                       useraccount number. This should never include private information like
     *                       credit card numbers or phone numbers.
     *
     * @see Rverbio#updateUserInfo(String, String)
     * @see Rverbio#updateUserEmail(String)
     */
    public void updateUserIdentifier(String userIdentifier) {
        EndUser endUser = new EndUser(RverbioUtils.getSupportId(_appContext));
        endUser.setUserIdentifier(userIdentifier);

        recordData(endUser);
    }

    /**
     * Show the feedback dialog
     *
     * @param activity The activity on which you wish to show the feedback dialog. This activity
     *                 must be a subclass of AppCompatActivity.
     */
    public void showDialog(@NonNull AppCompatActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_edit_name");

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        final RverbioFeedbackDialogFragment fragment = RverbioFeedbackDialogFragment.create(R.layout.rverb_fragment_dialog);
        fragment.show(manager, "fragment_edit_name");
    }

    /**
     * Takes a screenshot of the app as currently visible on the user's device. This
     * will give the developer context around the comments or questions submitted by the user.
     *
     * @param activity The activity from which you wish to take a screenshot.
     */
    public File getScreenshot(@NonNull Activity activity) {
        File screenshot = RverbioUtils.createScreenshotFile(activity);
        if (screenshot != null) {
            LogUtils.d("Screenshot File", screenshot.getAbsolutePath());

            Uri path = Uri.fromFile(screenshot);
            if (path != null) {
                return screenshot;
            }

            // TODO: The image uploadUrl expires after 30 minutes. If the image hasn't been uploaded
            // by then, delete the local copy. Make the timeout configurable.
        }

        return null;
    }

    private Rverbio initEndUser() {
        if (RverbioUtils.initializeSupportId(_appContext)) {
            recordData(new EndUser(getSupportId(_appContext)));
        }

        return this;
    }

    private Rverbio setSessionData() {
        String supportId = getSupportId(_appContext);
        String sessionId = UUID.randomUUID().toString();

        recordData(new Session(sessionId, supportId));

        return this;
    }

    private void sendQueuedRequests() {
        // Stap 1: find existing files of each DATA_TYPE
        File directory = _appContext.getCacheDir();
        File[] files = directory.listFiles();

        // Ensure we submit queued requests in the order they were made
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        // Step 2: loop through all found, attempting to resend
        for (File file : files) {
            String tempFilePath = file.getAbsolutePath();
            LogUtils.d("FileName", tempFilePath);

            Cacheable data = DataUtils.readObjectFromDisk(tempFilePath);
            if (data != null) {
                sendData(data, tempFilePath);
            }
        }
    }

    private void recordData(Cacheable data) {
        // Save data to file in case initial push fails
        String tempFileName = DataUtils.writeObjectToDisk(_appContext, data);
        sendData(data, tempFileName);
    }

    private void sendData(Cacheable data, String tempFileName) {
        _appContext.startService(data.getServiceIntent(_appContext, tempFileName));
    }
}
