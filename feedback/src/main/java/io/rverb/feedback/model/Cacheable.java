package io.rverb.feedback.model;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

public interface Cacheable extends Serializable {
    String getDataTypeDescriptor();
    Intent getServiceIntent(Context context, String cacheFileName);
}
