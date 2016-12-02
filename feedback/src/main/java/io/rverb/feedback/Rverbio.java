package io.rverb.feedback;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.UUID;

import io.rverb.feedback.data.api.SessionService;
import io.rverb.feedback.data.api.UpdateUserService;
import io.rverb.feedback.data.api.AddUserService;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Session;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.LogUtils;

import static io.rverb.feedback.RverbioUtils.getSupportId;

public class Rverbio {
    public static final String DATA_TYPE_USER_ADD = "enduser_add";
    public static final String DATA_TYPE_USER_UPDATE = "useremail_update";

    public static final String DATA_TYPE_SESSION = "session";

    private static Context _appContext;
    private static String _apiKey;

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
     * <p>
     * Initialization must be done before the Rverbio singleton can be used.
     *
     * @param context Activity or Application Context
     */
    public static void initialize(Context context) {
        _appContext = context.getApplicationContext();
        _apiKey = AppUtils.getApiKey(context);

        // Send any previously queued requests
        getInstance().sendQueuedRequests();
        getInstance().initEndUser().setSessionData();
    }

//    /**
//     * Sends the user's request to the developer.
//     *
//     * @param context Activity or Application Context
//     */
    // TODO: Allow dev-supplied context data
//    public void sendHelp(Context context) {
//        RverbioUtils.setSupportData(context);
//        // TODO: Handle custom NVPs
//
//        takeScreenshot(context);
//
//        Toast.makeText(context, "Help will come. Just not yet.", Toast.LENGTH_LONG).show();
//    }

    public void updateUserInfo(String emailAddress, String userIdentifier) {
        RverbioUtils.getSupportId(_appContext);

        EndUser endUser = new EndUser(RverbioUtils.getSupportId(_appContext));
        endUser.setEmailAddress(emailAddress);
        endUser.setUserIdentifier(userIdentifier);

        recordUpdateEndUser(endUser);
    }

    public void updateUserEmail(String emailAddress) {
        RverbioUtils.getSupportId(_appContext);

        EndUser endUser = new EndUser(RverbioUtils.getSupportId(_appContext));
        endUser.setEmailAddress(emailAddress);

        recordUpdateEndUser(endUser);
    }

    public void updateUserIdentifier(String userIdentifier) {
        RverbioUtils.getSupportId(_appContext);

        EndUser endUser = new EndUser(RverbioUtils.getSupportId(_appContext));
        endUser.setUserIdentifier(userIdentifier);

        recordUpdateEndUser(endUser);
    }

//    /**
//     * Takes a screenshot of the developer's app as currently visible on the user's device. This
//     * will give the developer context around the comments or questions submitted by the user.
//     *
//     * @param context Activity or Application Context
//     */
    // TODO: Allow on-demand screenshots?
//    public void takeScreenshot(Context context) {
//        File screenshot = RverbioUtils.createScreenshotFile(context);
//        if (screenshot != null) {
//            LogUtils.d("Screenshot File", screenshot.getAbsolutePath());
//
//            Uri path = Uri.fromFile(screenshot);
//            if (path != null) {
//                // TODO: Send screenshot
//            }
//
//            // Clean up after ourselves
//            screenshot.deleteOnExit(); // TODO: Only if the image got sent
//        }
//    }

    private Rverbio initEndUser() {
        if (RverbioUtils.initializeSupportId(_appContext)) {
            recordNewEndUser(new EndUser(getSupportId(_appContext)));
        }

        return this;
    }

    private Rverbio setSessionData() {
        String supportId = getSupportId(_appContext);
        String sessionId = UUID.randomUUID().toString();

        recordSessionStart(new Session(sessionId, supportId));

        return this;
    }

    private void sendQueuedRequests() {
        // Stap 1: find existing files of each DATA_TYPE
        File directory = _appContext.getCacheDir();
        File[] files = directory.listFiles();

        // Step 2: loop through all found, attempting to resend
        for (File file : files) {
            String tempFilePath = file.getAbsolutePath();
            LogUtils.d("FileName", tempFilePath);

            if (tempFilePath.toLowerCase().startsWith("rv_" + DATA_TYPE_SESSION)) {
                Session session = RverbioUtils.readObjectFromDisk(_appContext, tempFilePath, Session.class);
                if (session != null) {
                    getInstance().sendSessionData(session, tempFilePath);
                }
            } else if (tempFilePath.toLowerCase().startsWith("rv_" + DATA_TYPE_USER_ADD)) {
                EndUser endUser = RverbioUtils.readObjectFromDisk(_appContext, tempFilePath, EndUser.class);
                if (endUser != null) {
                    getInstance().sendEndUserAdd(endUser, tempFilePath);
                }
            } else if (tempFilePath.toLowerCase().startsWith("rv_" + DATA_TYPE_USER_UPDATE)) {
                EndUser endUser = RverbioUtils.readObjectFromDisk(_appContext, tempFilePath, EndUser.class);
                if (endUser != null) {
                    getInstance().sendEndUserAdd(endUser, tempFilePath);
                }
            }
        }
    }

    private void recordNewEndUser(EndUser endUser) {
        // Save data to file in case initial push fails
        String tempFileName = RverbioUtils.writeObjectToDisk(_appContext, DATA_TYPE_USER_ADD, endUser);
        sendEndUserAdd(endUser, tempFileName);
    }

    private void recordUpdateEndUser(EndUser endUser) {
        // Save data to file in case initial push fails
        String tempFileName = RverbioUtils.writeObjectToDisk(_appContext, DATA_TYPE_USER_UPDATE, endUser);
        sendEndUserUpdate(endUser, tempFileName);
    }

    private void recordSessionStart(Session session) {
        // Save data to file in case initial push fails
        String tempFileName = RverbioUtils.writeObjectToDisk(_appContext, DATA_TYPE_SESSION, session);
        sendSessionData(session, tempFileName);
    }

    private void sendEndUserAdd(EndUser endUser, String tempFileName) {
        Intent serviceIntent = new Intent(_appContext, AddUserService.class);

        serviceIntent.putExtra("api_key", _apiKey);
        serviceIntent.putExtra("temp_file_name", tempFileName);
        serviceIntent.putExtra("user_data", endUser);

        _appContext.startService(serviceIntent);
    }

    private void sendEndUserUpdate(EndUser endUser, String tempFileName) {
        Intent serviceIntent = new Intent(_appContext, UpdateUserService.class);

        serviceIntent.putExtra("api_key", _apiKey);
        serviceIntent.putExtra("temp_file_name", tempFileName);
        serviceIntent.putExtra("user_data", endUser);

        _appContext.startService(serviceIntent);
    }

    private void sendSessionData(Session session, String tempFileName) {
        Intent serviceIntent = new Intent(_appContext, SessionService.class);

        serviceIntent.putExtra("api_key", _apiKey);
        serviceIntent.putExtra("temp_file_name", tempFileName);
        serviceIntent.putExtra("session_data", session);

        _appContext.startService(serviceIntent);
    }
}
