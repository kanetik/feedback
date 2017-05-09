package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public interface IPersistable extends Serializable {
    String getDataTypeDescriptor();

    Intent getPersistServiceIntent(Context context, ResultReceiver resultReceiver);

    void incrementRetryCount();

    boolean retryAllowed();
}
