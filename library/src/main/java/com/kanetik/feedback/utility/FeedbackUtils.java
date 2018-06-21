package com.kanetik.feedback.utility;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.R;
import com.kanetik.feedback.model.ContextData;
import com.kanetik.feedback.model.ContextDataItem;
import com.kanetik.feedback.model.Feedback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class FeedbackUtils {
    private static final String SUPPORT_ID_KEY = "support_id";

    private static final String NO_NETWORK = "No Network";

    private static final String DATA_APP_VERSION = "App_Version";
    private static final String DATA_LOCALE = "Locale";
    private static final String DATA_MANUFACTURER = "Device_Manufacturer";
    private static final String DATA_MODEL = "Device_Model";
    private static final String DATA_DEVICE_NAME = "Device_Name";
    private static final String DATA_OS_VERSION = "OS_Version";
    private static final String DATA_NETWORK_TYPE = "Network_Type";

    public static ArrayList<ContextDataItem> getExtraData(Context context) {
        ArrayList<ContextDataItem> data = new ArrayList<>();

        data.add(new ContextDataItem(DATA_APP_VERSION, getVersionName(context) + " (" + getVersionCode(context) + ")"));
        data.add(new ContextDataItem(DATA_LOCALE, Locale.getDefault().toString()));
        data.add(new ContextDataItem(DATA_MANUFACTURER, Build.MANUFACTURER));
        data.add(new ContextDataItem(DATA_MODEL, Build.MODEL));
        data.add(new ContextDataItem(DATA_DEVICE_NAME, Build.PRODUCT));
        data.add(new ContextDataItem(DATA_OS_VERSION, Build.VERSION.RELEASE));
        data.add(new ContextDataItem(DATA_NETWORK_TYPE, getNetworkType(context)));

        return data;
    }

    public static void sendQueuedRequests(final Context context) {
        if (getNetworkType(context).equals(NO_NETWORK)) {
            return;
        }

        // Step 1: find existing files for the given DATA_TYPE
        File directory = context.getCacheDir();

        File[] files = directory.listFiles(pathname -> pathname.getName().startsWith("kanetik_feedback") && pathname.getName().endsWith("kf"));

        // Ensure we submit queued requests in the order they were made
        Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

        // Step 2: loop through all found, attempting to resend
        for (File file : files) {
            String tempFilePath = file.getAbsolutePath();

            if (KanetikFeedback.isDebug()) {
                LogUtils.i("FileName", tempFilePath);
            }

            final Feedback feedback = getQueuedFeedbackFromDisk(tempFilePath);
            if (feedback != null) {
                feedback.incrementRetryCount();

                deleteQueuedFeedback(tempFilePath);
                persistData(context, feedback, new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        if (resultCode != Activity.RESULT_OK) {
                            handlePersistenceFailure(context, feedback);
                        }
                    }
                });
            }
        }
    }

    public static void persistData(Context context, Feedback data, ResultReceiver resultReceiver) {
        context.startService(data.getSendServiceIntent(context, resultReceiver, data));
    }

    public static void handlePersistenceFailure(Context context, Feedback feedback) {
        if (feedback.retryAllowed()) {
            queueFeedbackToDisk(context, feedback);
        }
    }

    public static boolean validateTextEntryNotEmpty(@Nullable EditText field) {
        return field != null && !TextUtils.isEmpty(field.getText());
    }

    public static boolean validateTextEntryIsValid(@Nullable EditText field, @NonNull Pattern formatPattern) {
        return field != null && formatPattern.matcher(field.getText()).matches();
    }

    public static void addSystemData(Context context, Feedback feedback) {
        ContextData appData = new ContextData("App Info");
        appData.add("App Name", getAppLabel(context));
        appData.add("Package Name", context.getPackageName());
        appData.add("App Version", getVersionName(context));
        feedback.appData = appData;

        ContextData deviceData = new ContextData("Device Info");
        deviceData.add("Manufacturer", Build.MANUFACTURER);
        deviceData.add("Device Model", Build.MODEL);
        deviceData.add("Device Name", Build.PRODUCT);
        deviceData.add("Android Version", Build.VERSION.RELEASE);
        deviceData.add("Locale", Locale.getDefault().toString());
        deviceData.add("Network Type", getNetworkType(context));
        feedback.deviceData = deviceData;
    }

    public static void addInstanceContextDataToFeedback(Feedback feedback) {
        feedback.devData = new ContextData("Developer Info", KanetikFeedback.getContextData());
    }

    public static LiveData<String> getSupportId(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.kanetik.feedback.prefs", MODE_PRIVATE);

        String supportId = prefs.getString(SUPPORT_ID_KEY, "");
        if (TextUtils.isEmpty(supportId)) {
            supportId = UUID.randomUUID().toString();
            prefs.edit().putString(SUPPORT_ID_KEY, supportId).apply();
        }

        MutableLiveData<String> response = new MutableLiveData<>();
        response.postValue(supportId);

        return response;
    }

    public static String getAppLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;

        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "");
    }

    public static void alertUser(Context context) {
        Toast.makeText(context, R.string.kanetik_feedback_thanks, Toast.LENGTH_LONG).show();
    }

    private static boolean isNullOrWhiteSpace(String string) {
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

    private static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        String version = "Unknown";

        try {
            PackageInfo pi = packageManager.getPackageInfo(packageName, 0);
            if (pi != null && pi.versionName != null && !pi.versionName.equals("")) {
                version = pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    private static Integer getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        Integer version = null;

        try {
            PackageInfo pi = packageManager.getPackageInfo(packageName, 0);
            if (pi != null && pi.versionCode > 0) {
                version = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    private static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        if (activeNetwork != null && activeNetwork.isConnected()) {
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ? "WiFi" : "Not WiFi";
        } else {
            return NO_NETWORK;
        }
    }

    private static Feedback getQueuedFeedbackFromDisk(String fileName) {
        ObjectInputStream input;
        Feedback queuedObject = null;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object object = input.readObject();

            if (object instanceof Serializable) {
                queuedObject = (Feedback) object;
            }

            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queuedObject;
    }

    private static void queueFeedbackToDisk(Context context, Feedback feedback) {
        try {
            //create a temp file
            String fileName = "kanetik_feedback";

            File temp = File.createTempFile(fileName, ".kf", context.getCacheDir());
            FileOutputStream fos = getFileOutputStream(temp);

            if (fos != null) {
                ObjectOutputStream os = getObjectOutputStream(fos);

                if (os != null) {
                    os.writeObject(feedback);
                    os.close();
                    fos.close();
                }
            }
        } catch (IOException e) {
            // If this doesn't write, I think it's alright for now,
            // this is just a file to be checked on app start,
            // in case the initial API call failed.

            e.printStackTrace();
        }
    }

    private static FileOutputStream getFileOutputStream(File temp) {
        try {
            return new FileOutputStream(temp.getAbsolutePath());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private static ObjectOutputStream getObjectOutputStream(FileOutputStream fos) {
        try {
            return new ObjectOutputStream(fos);
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteQueuedFeedback(String fileName) {
        if (!isNullOrWhiteSpace(fileName)) {
            File file = new File(fileName);
            file.delete();
        }
    }
}