package io.rverb.feedback.utility;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;

import io.fabric.sdk.android.Fabric;
import io.rverb.feedback.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppUtils {
    public static final int FEEDBACK_SUBMITTED = 0;
    public static final int ANONYMOUS_FEEDBACK_SUBMITTED = 1;

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

    public static void openWebPage(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void notifyUser(Context context, int notificationType) {
        int notificationId = 1;

        String title = "";
        String content = "";

        if (notificationType == FEEDBACK_SUBMITTED) {
            title = "Feedback Sent";
            content = "Thanks! Your feedback has been sent - you should hear back soon.";
        } else if (notificationType == ANONYMOUS_FEEDBACK_SUBMITTED) {
            title = "Feedback Sent";
            content = "Thanks for your feedback!";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.rverb_logo_grayscale)
                .setContentTitle(title)
                .setContentText(content);

        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, builder.build());
    }

    @ColorInt
    public static int getToolbarThemeColor(@NonNull ActionBar supportActionBar, @AttrRes int attributeColor) {
        int colorAttr;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            colorAttr = supportActionBar.getThemedContext().getResources()
                    .getIdentifier("colorAccent", "attr", getPackageName(supportActionBar.getThemedContext()));
        }

        TypedValue outValue = new TypedValue();
        supportActionBar.getThemedContext().getTheme().resolveAttribute(colorAttr, outValue, true);

        return outValue.data;
    }

    public static void tintSupportBarIcon(@NonNull Context context, @NonNull MenuItem menuItem) {
        Drawable icon = menuItem.getIcon().mutate();
        DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.rverb_light_gray));
        menuItem.setIcon(icon);
    }

    public static void tintSupportBarIcon(@NonNull ActionBar supportActionBar, @NonNull MenuItem menuItem,
                                          @ColorRes @AttrRes int colorResource) {
        Drawable icon = menuItem.getIcon().mutate();
        DrawableCompat.setTint(icon, getToolbarThemeColor(supportActionBar, colorResource));
        menuItem.setIcon(icon);
    }

    public static Drawable tintDrawable(@NonNull Context context, @DrawableRes int drawableResource, @ColorRes int
            colorResource) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResource).mutate();
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorResource));

        return drawable;
    }

    public static boolean crashlyticsCapable() {
        try {
            Class c = Class.forName("io.fabric.sdk.android.Fabric");
            return c != null && Fabric.isInitialized();
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
