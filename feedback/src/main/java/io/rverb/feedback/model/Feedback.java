package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.support.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

import io.rverb.feedback.data.api.FeedbackService;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.DateUtils;

@Keep
public class Feedback extends Persistable {
    static final long serialVersionUID = 325L;

    public static String TYPE_DESCRIPTOR = "feedback";

    private String screenshotFileName;

    public String endUserId;
    public String timestampUtc;
    public String feedbackType;
    public ArrayList<DataItem> contextData;
    public String comment;
    public String appVersion;
    public String locale;
    public String deviceName;
    public String deviceManufacturer;
    public String deviceModel;
    public String osVersion;
    public String networkType;
    public String uploadUrl;
    public List<String> sessionStartsUtc;

    public Feedback(String endUserId, String feedbackType, String comment, String screenshotFileName) {
        this.endUserId = endUserId;
        this.feedbackType = feedbackType;
        this.comment = comment;
        this.screenshotFileName = screenshotFileName;
        this.timestampUtc = DateUtils.nowUtc();
        this.contextData = new ArrayList<>();
        this.sessionStartsUtc = new ArrayList<>();
    }

    @Override
    public int getRetryLimit() {
        return 0;
    }

    @Override
    public String getDataTypeDescriptor() {
        return TYPE_DESCRIPTOR;
    }

    @Override
    public Intent getPersistServiceIntent(Context context, ResultReceiver resultReceiver, IPersistable data) {
        Intent serviceIntent = new Intent(context, FeedbackService.class);

        serviceIntent.putExtra(DataUtils.EXTRA_RESULT_RECEIVER, resultReceiver);
        serviceIntent.putExtra(DataUtils.EXTRA_SELF, data);
        serviceIntent.putExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME, this.screenshotFileName);

        return serviceIntent;
    }

    @Override
    public String toString() {
        return "Comment: " + comment;
    }
}
