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
import io.rverb.feedback.utility.RverbioUtils;

public class Rverbio {
    static boolean _initialized = false;
    Context _appContext;
    SessionData _session;

    // SDK public method
    public static Rverbio getInstance() {
        return RverbioHolder.INSTANCE;
    }

    // SDK public method
    public static void initialize(Context context) {
        String supportId = RverbioUtils.initializeSupportId(context);
        String sessionId = UUID.randomUUID().toString();

        _initialized = true;

        getInstance().setAppContext(context).setSessionData(sessionId, supportId);
    }

    // SDK public method
    public static void initialize(Context context, String userIdentifier) {
        String supportId = RverbioUtils.initializeSupportId(context);
        String sessionId = UUID.randomUUID().toString();

        _initialized = false;

        getInstance().setAppContext(context).setSessionData(sessionId, supportId, userIdentifier);
    }

    // TODO: Allow dev-supplied context data
    // SDK public method
    public void sendHelp(Context context) {
        RverbioUtils.setSupportData(context);
        // TODO: Handle custom NVPs

        getScreenshot(context);

        Toast.makeText(context, "Help will come. Just not yet.", Toast.LENGTH_LONG).show();
    }

    // TODO: Allow on-demand screenshots?
    // SDK public method
    public void getScreenshot(Context context) {
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

    // SDK public method
    public String getSessionId() {
        return _session.sessionId;
    }

    // SDK public method
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
