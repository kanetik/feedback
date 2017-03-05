package io.rverb.feedback.model;

import android.support.annotation.Keep;

@Keep
public class Patch {
    public String op;
    public String path;
    public String value;

    public Patch(String op, String path, String value) {
        this.op = op;
        this.path = path;
        this.value = value;
    }
}
