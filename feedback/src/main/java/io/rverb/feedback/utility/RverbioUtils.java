package io.rverb.feedback.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;

import io.rverb.feedback.model.DataItem;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Event;
import io.rverb.feedback.model.Feedback;
import io.rverb.feedback.model.Persistable;
import io.rverb.feedback.model.Session;

import static io.rverb.feedback.utility.AppUtils.getPackageName;

public class RverbioUtils {
    public static final String DATA_APP_VERSION = "App_Version";
    public static final String DATA_LOCALE = "Locale";
    public static final String DATA_MANUFACTURER = "Device_Manufacturer";
    public static final String DATA_MODEL = "Device_Model";
    public static final String DATA_DEVICE_NAME = "Device_Name";
    public static final String DATA_OS_VERSION = "OS_Version";
    public static final String DATA_NETWORK_TYPE = "Network_Type";

    private static final String RVERBIO_PREFS = "rverbio";
    private static final String END_USER_KEY = "end_user";
    private static final String END_USER_ID_KEY = "end_user_id";
    private static final String APPLICATION_ID_KEY = "application_id";

    /*
        For a small set of users, I was recording "EndUser" with a prefs key of "EndUserId," but now
        I need EndUserId to actually be the EndUserId. So, for tnose users, I must migrate to the
        proper pref.
     */
    public static void migrateEndUserPrefs(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(END_USER_ID_KEY) && !prefs.contains(END_USER_KEY)) {
            // Move data from END_USER_ID_KEY to END_USER_KEY
            Gson gson = new Gson();
            EndUser userData = gson.fromJson(prefs.getString(END_USER_KEY, ""), EndUser.class);

            if (userData != null) {
                prefs.edit().putString(END_USER_KEY, gson.toJson(userData)).remove(END_USER_ID_KEY).apply();
            }
        }
    }

    public static String getApplicationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(APPLICATION_ID_KEY, "");
    }

    public static void saveApplicationId(Context context, String applicationId) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(APPLICATION_ID_KEY, applicationId).apply();
    }

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

    public static EndUser getEndUser(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        return gson.fromJson(prefs.getString(END_USER_KEY, ""), EndUser.class);
    }

    public static EndUser initEndUser(final Context context) {
        final EndUser endUser = new EndUser();
        saveEndUser(context, endUser);
        return endUser;
    }

    public static void saveEndUser(final Context context, final EndUser endUser) {
        cacheEndUser(context, endUser);

        saveEndUser(context, endUser, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode == Activity.RESULT_OK) {
                            endUser.isPersisted = true;

                            cacheEndUser(context, endUser);
                        }
                    }
                }
        );
    }

    public static void cacheEndUser(@NonNull Context context, @NonNull EndUser endUser) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(END_USER_KEY, gson.toJson(endUser)).apply();
    }

    private static String _sessionId;

    public static String getSessionId() {
        return _sessionId;
    }

    public static String getNewSessionId() {
        _sessionId = UUID.randomUUID().toString();
        return _sessionId;
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ? "WiFi" : "Not WiFi";
        } else {
            return "No Network";
        }
    }

    public static File takeScreenshot(Activity activity) {
        File screenshot = createScreenshotFile(activity);
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
            LogUtils.w(e.getMessage(), e);
        }

        return null;
    }

    public static String getApiKey(Context context) {
        ApplicationInfo ai = null;

        try {
            ai = context.getPackageManager().getApplicationInfo(getPackageName(context), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Bundle bundle = ai.metaData;
        return bundle.getString("io.rverb.apiKey");
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
        data.add(new DataItem(DATA_NETWORK_TYPE, RverbioUtils.getNetworkType(context)));

        return data;
    }

    public static void sendQueuedRequests(final Context context) {
        RverbioUtils.sendQueuedRequests(context, Session.TYPE_DESCRIPTOR);
        RverbioUtils.sendQueuedRequests(context, Event.TYPE_DESCRIPTOR);
        RverbioUtils.sendQueuedRequests(context, Feedback.TYPE_DESCRIPTOR);
    }

    public static void sendQueuedRequests(final Context context, final String dataTypeDescriptor) {
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
            String tempFilePath = file.getAbsolutePath();
            LogUtils.d("FileName", tempFilePath);

            final Persistable data = DataUtils.readObjectFromDisk(tempFilePath);
            if (data != null) {
                DataUtils.deleteFile(tempFilePath);
                persistData(context, data, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode != Activity.RESULT_OK) {
                            DataUtils.writeObjectToDisk(context, data);
                        }
                    }
                });
            }
        }
    }

    public static void persistData(Context context, Persistable data, ResultReceiver resultReceiver) {
        context.startService(data.getPersistServiceIntent(context, resultReceiver));
    }

    public static void saveEndUser(Context context, EndUser endUser, ResultReceiver resultReceiver) {
        context.startService(endUser.getPersistServiceIntent(context, resultReceiver));
    }
}