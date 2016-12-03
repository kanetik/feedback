package io.rverb.feedback.utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class AppUtils {
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getVersionName(Context context) {
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

    public static Integer getVersionCode(Context context) {
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

    static boolean isDebug(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }
}
