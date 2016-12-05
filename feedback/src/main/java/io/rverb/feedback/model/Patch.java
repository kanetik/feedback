package io.rverb.feedback.model;

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
