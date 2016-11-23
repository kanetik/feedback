package io.rverb.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.LogUtils;

public class RverbioUtils {
    public static final String DATA_TYPE_SESSION = "session";

    private static final String RVERBIO_PREFS = "rverbio";
    private static final String SUPPORT_ID_KEY = "support_id";

    public static String initializeSupportId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(RVERBIO_PREFS, Context.MODE_PRIVATE);

        String supportId = prefs.getString(SUPPORT_ID_KEY, "");
        if (TextUtils.isEmpty(supportId)) {
            supportId = UUID.randomUUID().toString();
            prefs.edit().putString(SUPPORT_ID_KEY, supportId).apply();
        }

        return supportId;
    }

    public static void setSupportData(Context context) {
        LogUtils.d("Manufacturer", Build.MANUFACTURER);
        LogUtils.d("Model", Build.MODEL);
        LogUtils.d("Device", Build.PRODUCT);
        LogUtils.d("Android Version", Build.VERSION.RELEASE);
        LogUtils.d("App Version", AppUtils.getVersionName(context));
        LogUtils.d("Support Identifier", initializeSupportId(context));

        // TODO: Send Support Data
    }

    public static File createScreenshotFile(Context context) {
        if (!(context instanceof Activity)) {
            return null;
        }

        try {
            File imageFile = File.createTempFile("rv_screenshot", ".png", context.getCacheDir());

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
            LogUtils.w(e.getMessage(), e);
        }

        return null;
    }

    public static <T> T readObjectFromDisk(Context context, String fileName, Class<T> type) {
        ObjectInputStream input;
        T queuedObject = null;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object object = input.readObject();

            if (object instanceof Serializable) {
                queuedObject = (T)object;
            }

            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queuedObject;
    }

    public static String writeObjectToDisk(Context context, String dataType, Serializable object) {
        try {
            //create a temp file
            String fileName = "rv_" + dataType;

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
        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(fileName);
            file.delete();
        }
    }
}