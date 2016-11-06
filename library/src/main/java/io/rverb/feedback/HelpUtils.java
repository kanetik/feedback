package io.rverb.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import io.rverb.feedback.models.ContextData;

public class HelpUtils {
    private static final String LOG_TAG = "rverb.io";
    private static final String SUPPORT_ID_KEY = "support_id";

    // SDK public method
    public static void sendHelp(Context context, ContextData contextData) {
        setSupportData(context);
        // TODO: Handle custom NVPs

        setScreenshot(context);

        Toast.makeText(context, "Help will come. Just not yet.", Toast.LENGTH_LONG).show();
    }

    // SDK public method
    public static String getSupportId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String supportId = prefs.getString(SUPPORT_ID_KEY, "");

        if (TextUtils.isEmpty(supportId)) {
            supportId = UUID.randomUUID().toString();
            prefs.edit().putString(SUPPORT_ID_KEY, supportId).apply();
        }

        return supportId;
    }

    static void setSupportData(Context context) {
        log(context, R.string.context_data_item_manufacturer_key, Build.MANUFACTURER);
        log(context, R.string.context_data_item_model_key, Build.MODEL);
        log(context, R.string.context_data_item_device_key, Build.PRODUCT);
        log(context, R.string.context_data_item_android_version_key, Build.VERSION.RELEASE);
        log(context, R.string.context_data_item_app_version_key, AppUtils.getVersionName(context));
        log(context, R.string.context_data_item_support_identifier_key, getSupportId(context));

        // TODO: Send Support Data
    }

    // TODO: Allow custom screenshots?
    static void setScreenshot(Context context) {
        File screenshot = saveScreenshot(context);
        if (screenshot != null) {
            log("Screenshot File", screenshot.getAbsolutePath());

            Uri path = Uri.fromFile(screenshot);
            if (path != null) {
                // TODO: Send screenshot
            }

            // Clean up after ourselves
            screenshot.deleteOnExit();
        }
    }

    static File saveScreenshot(Context context) {
        if (!(context instanceof Activity)) {
            return null;
        }

        try {
            File imageFile = File.createTempFile("rv_screenshot", ".png");

            View v1 = ((Activity) context).getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);

            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            FileOutputStream fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, fout);

            fout.flush();
            fout.close();

            return imageFile;
        } catch (IOException e) {
            Log.w(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    static void log(String message) {
        Log.d(LOG_TAG, message);
    }

    static void log(String key, String value) {
        Log.d(LOG_TAG, String.format(Locale.getDefault(), "%1$s: %2$s", key, value));
    }

    static void log(Context context, int key, String value) {
        Log.d(LOG_TAG, String.format(Locale.getDefault(), "%1$s: %2$s", context.getString(key), value));
    }
}