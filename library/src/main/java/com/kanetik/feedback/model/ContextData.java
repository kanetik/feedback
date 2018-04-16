package com.kanetik.feedback.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContextData implements Serializable {
    static final long serialVersionUID = 327L;

    String title;
    List<ContextDataItem> contextData;

    public ContextData(@NonNull String title) {
        this.title = title;
        this.contextData = new ArrayList<>();
    }

    public ContextData(@NonNull String title, @NonNull List<ContextDataItem> data) {
        this.title = title;
        this.contextData = new ArrayList<>(data);
    }

    public void add(String key, Object value) {
        this.contextData.add(new ContextDataItem(key, value));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(this.title + "\n\n");
        for (ContextDataItem contextItem : contextData) {
            builder.append(contextItem.key).append(": ").append(contextItem.value).append("\n");
        }

        return builder.toString();
    }
}
