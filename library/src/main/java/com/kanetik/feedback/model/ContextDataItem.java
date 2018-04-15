package com.kanetik.feedback.model;

import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public class ContextDataItem implements Serializable {
    static final long serialVersionUID = 326L;

    public String key;
    public Object value;

    public ContextDataItem(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ContextDataItem)) {
            return false;
        }

        ContextDataItem that = (ContextDataItem) other;
        return this.key.equals(that.key);
    }
}
