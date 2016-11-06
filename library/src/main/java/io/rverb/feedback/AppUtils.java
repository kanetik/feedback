package io.rverb.feedback;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

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
            if (pi != null && !TextUtils.isEmpty(pi.versionName)) {
                version = pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static boolean isDebug(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }
}
