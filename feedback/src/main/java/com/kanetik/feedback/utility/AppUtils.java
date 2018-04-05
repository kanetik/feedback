package com.kanetik.feedback.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;

import com.kanetik.feedback.R;

public class AppUtils {
    static final String NO_NETWORK = "No Network";

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

    public static String getNetworkType(Context context) {
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

    @ColorInt
    private static int getToolbarThemeColor(@NonNull ActionBar supportActionBar, @AttrRes int attributeColor) {
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
        DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.kanetik_feedback_light_gray));
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

    public static void alertUser(Context context) {
        Toast.makeText(context, R.string.kanetik_feedback_thanks, Toast.LENGTH_LONG).show();
    }
}
