package io.rverb.feedback.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static io.rverb.feedback.utility.AppUtils.getPackageName;

public class RverbioUtils {
    public static final String EXTRA_DATA_APP_VERSION = "App_Version";
    public static final String EXTRA_DATA_LOCALE = "Locale";
    public static final String EXTRA_DATA_MANUFACTURER = "Device_Manufacturer";
    public static final String EXTRA_DATA_MODEL = "Device_Model";
    public static final String EXTRA_DATA_DEVICE_NAME = "Device_Name";
    public static final String EXTRA_DATA_OS_VERSION = "OS_Version";
    public static final String EXTRA_DATA_NETWORK_TYPE = "Network_Type";

    private static final String RVERBIO_PREFS = "rverbio";
    private static final String SUPPORT_ID_KEY = "support_id";
    private static final String EMAIL_ADDRESS_KEY = "email_address";
    private static final String APPLICATION_ID_KEY = "application_id";

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

    public static void saveEndUserEmailAddress(Context context, String emailAddress) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(EMAIL_ADDRESS_KEY, emailAddress).apply();
    }

    public static String retrieveEndUserEmailAddress(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(EMAIL_ADDRESS_KEY, "");
    }

    public static void saveApplicationId(Context context, String applicationId) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(APPLICATION_ID_KEY, applicationId).apply();
    }

    public static String retrieveApplicationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(APPLICATION_ID_KEY, "");
    }

    public static boolean emailAddressKnown(Context context) {
        try {
            return !TextUtils.isEmpty(retrieveEndUserEmailAddress(context));
        } catch (Exception ex) {
            return false;
        }
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

    public static Map<String, String> getExtraData(Context context) {
        Map<String, String> data = new ArrayMap<>();

        data.put(EXTRA_DATA_APP_VERSION, AppUtils.getVersionName(context) + " (" + AppUtils.getVersionCode(context) + ")");
        data.put(EXTRA_DATA_LOCALE, Locale.getDefault().toString());
        data.put(EXTRA_DATA_MANUFACTURER, Build.MANUFACTURER);
        data.put(EXTRA_DATA_MODEL, Build.MODEL);
        data.put(EXTRA_DATA_DEVICE_NAME, Build.PRODUCT);
        data.put(EXTRA_DATA_OS_VERSION, Build.VERSION.RELEASE);
        data.put(EXTRA_DATA_NETWORK_TYPE, RverbioUtils.getNetworkType(context));

        return data;
    }
}