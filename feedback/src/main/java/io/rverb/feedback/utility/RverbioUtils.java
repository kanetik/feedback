package io.rverb.feedback.utility;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.rverb.feedback.R;
import io.rverb.feedback.Rverbio;
import io.rverb.feedback.model.DataItem;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Event;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.model.IPersistable;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RverbioUtils {
    private static final String DATA_APP_VERSION = "App_Version";
    private static final String DATA_LOCALE = "Locale";
    private static final String DATA_MANUFACTURER = "Device_Manufacturer";
    private static final String DATA_MODEL = "Device_Model";
    private static final String DATA_DEVICE_NAME = "Device_Name";
    private static final String DATA_OS_VERSION = "OS_Version";
    private static final String DATA_NETWORK_TYPE = "Network_Type";

    private static final String RVERBIO_PREFS = "io.rverb.feedback.prefs";
    private static final String END_USER_KEY = "end_user";
    private static final String SESSION_START = "session_start_utc";

    public static final String RVERBIO_NOTIFICATION_CHANNEL_ID = "rverbio";

    public static final int FEEDBACK_SUBMITTED = 0;
    public static final int ANONYMOUS_FEEDBACK_SUBMITTED = 1;

    public static boolean isNullOrWhiteSpace(String string) {
        if (string == null) {
            return true;
        }

        int stringLength = string.length();
        if (stringLength == 0) {
            return true;
        }

        for (int i = 0; i < stringLength; i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static void setEndUser(@NonNull Context context, @NonNull EndUser endUser) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(END_USER_KEY, gson.toJson(endUser)).apply();
    }

    public static EndUser getEndUser(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        return gson.fromJson(prefs.getString(END_USER_KEY, ""), EndUser.class);
    }

    public static void setSessionStart(@NonNull Context context) {
        List<Long> sessionStarts = getSessionStarts(context);
        if (sessionStarts == null) {
            sessionStarts = new ArrayList<>();
        } else {
            // Clean up older data
            List<Long> temp = new ArrayList<>();

            for (long sessionStart : sessionStarts) {
                if (sessionStart < DateUtils.weekAgoInMillis()) {
                    break;
                }

                temp.add(sessionStart);
            }

            sessionStarts = temp;
        }

        sessionStarts.add(DateUtils.currentInMillisUtc());

        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(SESSION_START, gson.toJson(sessionStarts)).apply();
    }

    public static List<Long> getSessionStarts(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        String sessionStarts = prefs.getString(SESSION_START, "");

        if (!TextUtils.isEmpty(sessionStarts)) {
            return gson.fromJson(sessionStarts, new TypeToken<List<Long>>() {
            }.getType());
        }

        return new ArrayList<>();
    }

    public static List<String> getSessionStartTimestamps(Context context) {
        List<Long> starts = getSessionStarts(context);
        List<String> timestamps = new ArrayList<>();

        for (long start : starts) {
            timestamps.add(DateUtils.millisToDate(start));
        }

        return timestamps;
    }

    public static File takeScreenshot(Activity activity) {
        File screenshot = createScreenshotFile(activity);
        if (screenshot != null) {
            if (Rverbio.isDebug()) {
                LogUtils.i("Screenshot File", screenshot.getAbsolutePath());
            }

            Uri path = Uri.fromFile(screenshot);
            if (path != null) {
                return screenshot;
            }

            // TODO: The image uploadUrl expires after 30 minutes. If the image hasn't been uploaded
            // by then, delete the local copy. Make the timeout configurable.
        }

        return null;
    }

    public static File createScreenshotFile(@NonNull Activity activity) {
        try {
            File imageFile = File.createTempFile("rv_screenshot", ".jpg", activity.getCacheDir());

            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            v1.buildDrawingCache(true);

            if (v1.getDrawingCache() == null) {
                return null;
            }

            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());

            FileOutputStream fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fout);

            fout.flush();
            fout.close();

            v1.destroyDrawingCache();

            return imageFile;
        } catch (IOException e) {
            if (Rverbio.isDebug()) {
                LogUtils.w(e.getMessage(), e);
            }
        }

        return null;
    }

    public static ArrayList<DataItem> getExtraData(Context context) {
        ArrayList<DataItem> data = new ArrayList<>();

        data.add(new DataItem(DATA_APP_VERSION,
                AppUtils.getVersionName(context) + " (" + AppUtils.getVersionCode(context) + ")"));
        data.add(new DataItem(DATA_LOCALE, Locale.getDefault().toString()));
        data.add(new DataItem(DATA_MANUFACTURER, Build.MANUFACTURER));
        data.add(new DataItem(DATA_MODEL, Build.MODEL));
        data.add(new DataItem(DATA_DEVICE_NAME, Build.PRODUCT));
        data.add(new DataItem(DATA_OS_VERSION, Build.VERSION.RELEASE));
        data.add(new DataItem(DATA_NETWORK_TYPE, AppUtils.getNetworkType(context)));

        return data;
    }

    public static boolean sendQueuedRequests(final Context context) {
        boolean hadQueuedFeedback = false;

        if (AppUtils.getNetworkType(context).equals(AppUtils.NO_NETWORK)) {
            return false;
        }

        RverbioUtils.sendQueuedRequests(context, Event.TYPE_DESCRIPTOR);
        hadQueuedFeedback = RverbioUtils.sendQueuedRequests(context, Feedback.TYPE_DESCRIPTOR);

        return hadQueuedFeedback;
    }

    private static boolean sendQueuedRequests(final Context context, final String dataTypeDescriptor) {
        boolean hadQueuedRequests = false;

        // Stap 1: find existing files for the given DATA_TYPE
        File directory = context.getCacheDir();

        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("rv_" + dataTypeDescriptor) && pathname.getName().endsWith("rvb");
            }
        });

        // Ensure we submit queued requests in the order they were made
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        // Step 2: loop through all found, attempting to resend
        for (File file : files) {
            hadQueuedRequests = true;

            String tempFilePath = file.getAbsolutePath();

            if (Rverbio.isDebug()) {
                LogUtils.i("FileName", tempFilePath);
            }

            final IPersistable data = DataUtils.readObjectFromDisk(tempFilePath);
            if (data != null) {
                data.incrementRetryCount();

                DataUtils.deleteFile(tempFilePath);
                persistData(context, data, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode != Activity.RESULT_OK) {
                            handlePersistenceFailure(context, data);
                        }
                    }
                });
            }
        }

        return hadQueuedRequests;
    }

    public static void persistData(Context context, IPersistable data, ResultReceiver resultReceiver) {
        context.startService(data.getPersistServiceIntent(context, resultReceiver, data));
    }

    public static void persistEndUser(final Context context) {
        final EndUser endUser = getEndUser(context);
        if (endUser == null || RverbioUtils.isNullOrWhiteSpace(endUser.endUserId)) {
            throw new IllegalStateException("You must call Rverbio#initialize to initialize the EndUser",
                    new Throwable("Rverbio instance not initialized"));
        }

        ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    endUser.isPersisted = true;
                    endUser.isSynced = true;

                    setEndUser(context, endUser);
                }
            }
        };

        context.startService(endUser.getPersistServiceIntent(context, resultReceiver));
    }

    public static void handlePersistenceFailure(Context context, IPersistable data) {
        if (data.retryAllowed()) {
            DataUtils.writeObjectToDisk(context, data);
        }
    }

    public static void alertUser(Context context, int alertType) {
        int notificationId = 1;

        String title = "";
        String content = "";

        if (alertType == FEEDBACK_SUBMITTED) {
            title = "Thank You!";
            content = "Your feedback has been sent - you should hear back soon.";
        } else if (alertType == ANONYMOUS_FEEDBACK_SUBMITTED) {
            title = "Feedback Sent";
            content = "Thanks for your feedback!";
        }

        boolean alertShown = false;
        if (Rverbio.getInstance(context).getOptions().useNotifications()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, RVERBIO_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.rverb_logo_grayscale)
                    .setContentTitle(title)
                    .setContentText(content);

            NotificationManager notifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (notifyMgr != null) {
                notifyMgr.notify(notificationId, builder.build());
                alertShown = true;
            }
        }

        if (!alertShown) {
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
        }
    }
}