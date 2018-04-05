package com.kanetik.feedback.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContextData implements Serializable {
    static final long serialVersionUID = 327L;

    String title;
    List<ContextDataItem> contextData;

    public ContextData(String title) {
        this.title = title;
        this.contextData = new ArrayList<>();
    }

    public void add(String key, Object value) {
        this.contextData.add(new ContextDataItem(key, value));
    }

    public String toHtml() {
        StringBuilder builder = new StringBuilder("<h3>" + this.title + "</h3>");
        for (ContextDataItem contextItem : contextData) {
            builder.append("<b>").append(contextItem.key).append("</b>: ").append(contextItem.value).append("<br>");
        }

        return builder.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(this.title + "\n\n");
        for (ContextDataItem contextItem : contextData) {
            builder.append(contextItem.key).append(": ").append(contextItem.value).append("\n");
        }

        return builder.toString();
    }
}
