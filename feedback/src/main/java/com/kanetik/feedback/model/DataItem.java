package com.kanetik.feedback.model;

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

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DataItem)) {
            return false;
        }

        DataItem that = (DataItem) other;
        return this.key.equals(that.key);
    }
}
