package io.rverb.feedback;

public class Rverbio {
    private Rverbio() {
        // TODO: check for un-sent message/data
        // TODO: log supportId was seen
    }

    private static class RverbioHolder {
        private static final Rverbio INSTANCE = new Rverbio();
    }

    public static Rverbio getInstance() {
        return RverbioHolder.INSTANCE;
    }
}
