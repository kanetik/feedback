package com.kanetik.feedback.utility;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.model.ContextData;
import com.kanetik.feedback.model.ContextDataItem;
import com.kanetik.feedback.model.Feedback;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

public class FeedbackUtils {
    private static final int CONTENT_TYPE_PLAIN_TEXT = 0;
    private static final int CONTENT_TYPE_HTML = 1;

    private static final String DATA_APP_VERSION = "App_Version";
    private static final String DATA_LOCALE = "Locale";
    private static final String DATA_MANUFACTURER = "Device_Manufacturer";
    private static final String DATA_MODEL = "Device_Model";
    private static final String DATA_DEVICE_NAME = "Device_Name";
    private static final String DATA_OS_VERSION = "OS_Version";
    private static final String DATA_NETWORK_TYPE = "Network_Type";

    public static ArrayList<ContextDataItem> getExtraData(Context context) {
        ArrayList<ContextDataItem> data = new ArrayList<>();

        data.add(new ContextDataItem(DATA_APP_VERSION,
                AppUtils.getVersionName(context) + " (" + AppUtils.getVersionCode(context) + ")"));
        data.add(new ContextDataItem(DATA_LOCALE, Locale.getDefault().toString()));
        data.add(new ContextDataItem(DATA_MANUFACTURER, Build.MANUFACTURER));
        data.add(new ContextDataItem(DATA_MODEL, Build.MODEL));
        data.add(new ContextDataItem(DATA_DEVICE_NAME, Build.PRODUCT));
        data.add(new ContextDataItem(DATA_OS_VERSION, Build.VERSION.RELEASE));
        data.add(new ContextDataItem(DATA_NETWORK_TYPE, AppUtils.getNetworkType(context)));

        return data;
    }

    public static boolean sendQueuedRequests(final Context context) {
        boolean hadQueuedRequests = false;

        if (AppUtils.getNetworkType(context).equals(AppUtils.NO_NETWORK)) {
            return false;
        }

        // Stap 1: find existing files for the given DATA_TYPE
        File directory = context.getCacheDir();

        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("kanetik_feedback") && pathname.getName().endsWith("kf");
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

        return hadQueuedRequests;
    }

    public static Feedback getQueuedFeedbackFromDisk(String fileName) {
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

    public static String queueFeedbackToDisk(Context context, Feedback feedback) {
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

                    return temp.getAbsolutePath();
                }
            }

            return null;
        } catch (IOException e) {
            // If this doesn't write, I think it's alright for now,
            // this is just a file to be checked on app start,
            // in case the initial API call failed.

            e.printStackTrace();
            return null;
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

    public static void persistData(Context context, Feedback data, ResultReceiver resultReceiver) {
        context.startService(data.getSendServiceIntent(context, resultReceiver, data));
    }

    public static void handlePersistenceFailure(Context context, Feedback feedback) {
        if (feedback.retryAllowed()) {
            queueFeedbackToDisk(context, feedback);
        }
    }

    private static void deleteQueuedFeedback(String fileName) {
        if (!AppUtils.isNullOrWhiteSpace(fileName)) {
            File file = new File(fileName);
            file.delete();
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
        appData.add("App Name", AppUtils.getAppLabel(context));
        appData.add("Package Name", AppUtils.getPackageName(context));
        appData.add("App Version", AppUtils.getVersionName(context));
        feedback.appData = appData;

        ContextData deviceData = new ContextData("Device Info");
        deviceData.add("Manufacturer", Build.MANUFACTURER);
        deviceData.add("Device Model", Build.MODEL);
        deviceData.add("Device Name", Build.PRODUCT);
        deviceData.add("Android Version", Build.VERSION.RELEASE);
        deviceData.add("Locale", Locale.getDefault().toString());
        deviceData.add("Network Type", AppUtils.getNetworkType(context));
        feedback.deviceData = deviceData;
    }

//    public static String getSystemDataString(int contentType, Feedback feedback) {
//        String labelFormat = "%s: ";
//        String valueFormat = " %s";
//        String lineDelimiter = "\n";
//
//        if (contentType == CONTENT_TYPE_HTML) {
//            labelFormat = "<dd>%s</dd>";
//            valueFormat = "<dt>%s</dt>";
//            lineDelimiter = "";
//        }
//
//        StringBuilder builder = new StringBuilder();
//        for (Map.Entry<String, Object> contextItem : feedback.appData) {
//
//        }
//
//        return String.format(Locale.US, labelFormat, "App Version") + String.format(Locale.US, valueFormat, feedback.appVersion) + lineDelimiter +
//                String.format(Locale.US, labelFormat, "User Locale") + String.format(Locale.US, valueFormat, feedback.locale) + lineDelimiter +
//                String.format(Locale.US, labelFormat, "Device Manufacturer") + String.format(Locale.US, valueFormat, feedback.deviceManufacturer) + lineDelimiter +
//                String.format(Locale.US, labelFormat, "Device Model") + String.format(Locale.US, valueFormat, feedback.deviceModel) + lineDelimiter +
//                String.format(Locale.US, labelFormat, "Device Name") + String.format(Locale.US, valueFormat, feedback.deviceName) + lineDelimiter +
//                String.format(Locale.US, labelFormat, "Android Version") + String.format(Locale.US, valueFormat, feedback.osVersion) + lineDelimiter +
//                String.format(Locale.US, labelFormat, "Network Type") + String.format(Locale.US, valueFormat, feedback.networkType) + lineDelimiter;
//    }

//    public static void addInstanceContextDataToFeedback(Feedback feedback, ArrayList<ContextDataItem> contextData) {
//        if (contextData != null) {
//            feedback.contextData = contextData;
//        }
//    }
}