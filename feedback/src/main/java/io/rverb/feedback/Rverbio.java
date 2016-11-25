package io.rverb.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

import io.rverb.feedback.data.api.SessionService;
import io.rverb.feedback.model.SessionData;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.LogUtils;

public class Rverbio {
    private static Context _appContext;
    private static String _apiKey;

    private static final Rverbio _instance = new Rverbio();

    private SessionData _session;
    private String _userIdentifier;

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

        getInstance().sendQueuedRequests();
        getInstance().setSessionData();
    }

    /**
     * Sends the user's request to the developer.
     *
     * @param context Activity or Application Context
     */
    // TODO: Allow dev-supplied context data
    public void sendHelp(Context context) {
        RverbioUtils.setSupportData(context);
        // TODO: Handle custom NVPs

        takeScreenshot(context);

        Toast.makeText(context, "Help will come. Just not yet.", Toast.LENGTH_LONG).show();
    }

    /**
     * Takes a screenshot of the developer's app as currently visible on the user's device. This
     * will give the developer context around the comments or questions submitted by the user.
     *
     * @param context Activity or Application Context
     */
    // TODO: Allow on-demand screenshots?
    public void takeScreenshot(Context context) {
        File screenshot = RverbioUtils.createScreenshotFile(context);
        if (screenshot != null) {
            LogUtils.d("Screenshot File", screenshot.getAbsolutePath());

            Uri path = Uri.fromFile(screenshot);
            if (path != null) {
                // TODO: Send screenshot
            }

            // Clean up after ourselves
            screenshot.deleteOnExit(); // TODO: Only if the image got sent
        }
    }

    /**
     * An identifier that is meaningful to the developer, such as an account ID or an email address.
     * This will enable rverb.io to link a user across devices, and will enable the developer to
     * search for all requests made by a single user.
     *
     * @param userIdentifier A string which contains the identifying information. This should NOT
     *                       contain sensitive information like Social Security Number or credit
     *                       card numbers.
     */
    public Rverbio setUserIdentifier(String userIdentifier) {
        _userIdentifier = userIdentifier;
        return this;

        // TODO Submit id
    }

    private Rverbio setSessionData() {
        String supportId = RverbioUtils.initializeSupportId(_appContext);
        String sessionId = UUID.randomUUID().toString();

        _session = new SessionData(AppUtils.getPackageName(_appContext), sessionId, supportId);
        if (!TextUtils.isEmpty(_userIdentifier)) {
            _session.userIdentifier = _userIdentifier;
        }

        recordSessionStart();

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
            SessionData sessionData = RverbioUtils.readObjectFromDisk(_appContext, tempFilePath, SessionData.class);

            if (sessionData != null) {
                getInstance().sendSessionData(sessionData, tempFilePath);
            }
        }
    }

    private void recordSessionStart() {
        // Save data to file in case initial push fails
        String tempFileName = RverbioUtils.writeObjectToDisk(_appContext, RverbioUtils.DATA_TYPE_SESSION, _session);
        sendSessionData(_session, tempFileName);
    }

    private void sendSessionData(SessionData session, String tempFileName) {
        Intent serviceIntent = new Intent(_appContext, SessionService.class);
        serviceIntent.putExtra("api_key", _apiKey);
        serviceIntent.putExtra("session_data", session);
        serviceIntent.putExtra("temp_file_name", tempFileName);

        _appContext.startService(serviceIntent);
    }
}
