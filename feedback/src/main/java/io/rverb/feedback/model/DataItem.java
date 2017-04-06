package io.rverb.feedback.model;

import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public class DataItem implements Serializable {
    static final long serialVersionUID = 362L;

    public String key;
    public Object value;

    public DataItem(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
