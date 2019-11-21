package com.kanetik.feedback.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ContextData implements Serializable {
    static final long serialVersionUID = 327L;

    private String title;
    private List<ContextDataItem> contextData;

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
