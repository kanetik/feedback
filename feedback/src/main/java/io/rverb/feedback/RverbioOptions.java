package io.rverb.feedback;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import io.rverb.feedback.utility.RverbioUtils;

@Keep
public class RverbioOptions {
    private boolean mAttachScreenshotByDefault;
    private boolean mUseNotifications;

    public RverbioOptions() {
        mAttachScreenshotByDefault = true;
        mUseNotifications = Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
    }

    public boolean attachScreenshotByDefault() {
        return mAttachScreenshotByDefault;
    }

    public RverbioOptions setAttachScreenshotEnabled(boolean attachScreenshot) {
        mAttachScreenshotByDefault = attachScreenshot;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public RverbioOptions setUseNotifications(boolean useNotifications) throws IllegalStateException {
        if (!useNotifications || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mUseNotifications = useNotifications;
        } else {
            NotificationManager notificationManager = (NotificationManager) Rverbio._appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null && notificationManager.getNotificationChannel(RverbioUtils.RVERBIO_NOTIFICATION_CHANNEL_ID) != null) {
                mUseNotifications = true;
            } else {
                throw new IllegalStateException("You must first call RverbioOptions#setNotificationChannel before setting alerts to use notifications");
            }
        }

        return this;
    }

    public boolean useNotifications() {
        return mUseNotifications;
    }

    public RverbioOptions setNotificationChannel() {
        return setNotificationChannel(null, null);
    }

    public RverbioOptions setNotificationChannel(@Nullable String channelName, @Nullable String channelDescription) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String name = Rverbio._appContext.getString(R.string.rverb_default_notification_channel_name);
            if (!TextUtils.isEmpty(channelName)) {
                name = channelName;
            }

            String description = Rverbio._appContext.getString(R.string.rverb_default_notification_channel_description);
            if (!TextUtils.isEmpty(channelDescription)) {
                description = channelDescription;
            }

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(RverbioUtils.RVERBIO_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) Rverbio._appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        return this;
    }
}
