package io.rverb.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import io.rverb.feedback.data.api.SessionService;
import io.rverb.feedback.model.SessionData;
import io.rverb.feedback.utility.LogUtils;

public class Rverbio {
    static boolean _initialized = false;
    Context _appContext;
    SessionData _session;

    /**
     * Gets the rverb.io singleton, which is the primary interaction point the developer will have
     * with the rverb.io SDK.
     *
     * @return Rverbio singleton instance.
     */
    public static Rverbio getInstance() {
        return RverbioHolder.INSTANCE;
    }

    /**
     * Initializes the Rverbio singleton. The developer's interactions with rverb.io will be
     * entirely via the singleton.
     * <p>
     * Initialization must be done before the Rverbio singleton can be used.
     *
     * @param context Activity or Application Context
     * @see Rverbio#initialize(Context, String)
     */
    public static void initialize(Context context) {
        String supportId = RverbioUtils.initializeSupportId(context);
        String sessionId = UUID.randomUUID().toString();

        _initialized = true;

        getInstance().setAppContext(context).setSessionData(sessionId, supportId);
    }

    /**
     * Initializes the Rverbio singleton. The developer's interactions with rverb.io will be
     * entirely via the singleton.
     * <p>
     * Initialization must be done before the Rverbio singleton can be used.
     *
     * @param context        Activity or Application Context
     * @param userIdentifier A string which contains an identifier that is meaningful to the
     *                       developer. This should NOT contain sensitive information like Social
     *                       Security Number or credit card numbers.
     * @see Rverbio#setUserIdentifier(String)
     * @see Rverbio#initialize(Context)
     */
    public static void initialize(Context context, String userIdentifier) {
        String supportId = RverbioUtils.initializeSupportId(context);
        String sessionId = UUID.randomUUID().toString();

        _initialized = false;

        getInstance().setAppContext(context).setSessionData(sessionId, supportId, userIdentifier);
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
    public void setUserIdentifier(String userIdentifier) {
        _session.userIdentifier = userIdentifier;

        // TODO Submit id
    }

    Rverbio() {
        if (!_initialized) {
            throw new IllegalStateException("You must call Rverbio#initialize before accessing the Rverbio instance",
                    new Throwable("Rverbio instance not initialized"));
        }

        // TODO: check for un-sent message/data
    }

    private static class RverbioHolder {
        private static final Rverbio INSTANCE = new Rverbio();
    }

    private Rverbio setAppContext(Context context) {
        _appContext = context.getApplicationContext();
        return this;
    }

    private Rverbio setSessionData(String sessionId, String supportId) {
        _session = new SessionData(sessionId, supportId);
        sendSessionData();

        return this;
    }

    private Rverbio setSessionData(String sessionId, String supportId, String userIdentifier) {
        _session = new SessionData(sessionId, supportId, userIdentifier);
        sendSessionData();

        return this;
    }

    private void sendSessionData() {
        // TODO: Save data to file in case initial push fails
        String tempFileName = RverbioUtils.writeObjectToDisk(_appContext, RverbioUtils.DATA_TYPE_SESSION, _session);

        Intent serviceIntent = new Intent(_appContext, SessionService.class);
        serviceIntent.putExtra("session_data", _session);
        serviceIntent.putExtra("temp_file_name", tempFileName);

        _appContext.startService(serviceIntent);
    }

    private String now() {
        Calendar c = GregorianCalendar.getInstance();
        DateFormat df = SimpleDateFormat.getDateTimeInstance();
        return df.format(c.getTime());
    }
}
