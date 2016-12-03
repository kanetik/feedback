package io.rverb.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.rverb.feedback.model.Cacheable;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.LogUtils;

public class RverbioUtils {
    private static final String RVERBIO_PREFS = "rverbio";
    private static final String SUPPORT_ID_KEY = "support_id";

    private static String _newSupportId;

    public static boolean initializeSupportId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        String supportId = prefs.getString(SUPPORT_ID_KEY, "");

        if (isNullOrWhiteSpace(supportId)) {
            _newSupportId = UUID.randomUUID().toString();
            prefs.edit().putString(SUPPORT_ID_KEY, _newSupportId).apply();

            return true;
        }

        return false;
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

    public static String getSupportId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        String supportId = prefs.getString(SUPPORT_ID_KEY, "");

        if (isNullOrWhiteSpace(supportId)) {
            throw new IllegalStateException("You must call Rverbio#initialize before accessing the SupportId",
                    new Throwable("Rverbio instance not initialized"));
        }

        return supportId;
    }

    public static void addSystemData(Context context, Map<String, String> params) {
        params.put("appVersion", AppUtils.getVersionName(context) + " (" + AppUtils.getVersionCode(context) + ")");
        params.put("locale", Locale.getDefault().toString());
        params.put("deviceManufacturer", Build.MANUFACTURER);
        params.put("deviceModel", Build.MODEL);
        params.put("deviceName", Build.PRODUCT);
        params.put("osVersion", Build.VERSION.RELEASE);
        params.put("networkType", RverbioUtils.getNetworkType(context));
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ? "WiFi" : "Not WiFi";
    }

    public static File createScreenshotFile(@NonNull Activity activity) {
        try {
            File imageFile = File.createTempFile("rv_screenshot", ".png", activity.getCacheDir());

            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);

            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            FileOutputStream fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, fout);

            fout.flush();
            fout.close();

            return imageFile;
        } catch (IOException e) {
            LogUtils.w(e.getMessage(), e);
        }

        return null;
    }

    public static Cacheable readObjectFromDisk(String fileName) {
        ObjectInputStream input;
        Cacheable queuedObject = null;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object object = input.readObject();

            if (object instanceof Serializable) {
                queuedObject = (Cacheable) object;
            }

            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queuedObject;
    }

    public static String writeObjectToDisk(Context context, Cacheable object) {
        try {
            //create a temp file
            String fileName = "rv_" + object.getTempFileNameTag();

            File temp = File.createTempFile(fileName, ".tmp", context.getCacheDir());
            FileOutputStream fos = getFileOutputStream(temp);

            if (fos != null) {
                ObjectOutputStream os = getObjectOutputStream(fos);

                if (os != null) {
                    os.writeObject(object);
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

    protected static FileOutputStream getFileOutputStream(File temp) {
        try {
            return new FileOutputStream(temp.getAbsolutePath());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    protected static ObjectOutputStream getObjectOutputStream(FileOutputStream fos) {
        try {
            return new ObjectOutputStream(fos);
        } catch (IOException e) {
            return null;
        }
    }

    public static void deleteFile(String fileName) {
        if (!isNullOrWhiteSpace(fileName)) {
            File file = new File(fileName);
            file.delete();
        }
    }
}