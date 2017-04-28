package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import java.io.Serializable;

public interface Persistable extends Serializable {
    String getDataTypeDescriptor();
    Intent getPersistServiceIntent(Context context, ResultReceiver resultReceiver);
}
