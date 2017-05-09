package io.rverb.feedback.model;

import android.support.annotation.Keep;

@Keep
public abstract class Persistable implements IPersistable {
    private int retryLimit = 1;
    private int retryCount;

    public int getRetryLimit() {
        return retryLimit;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean retryAllowed() {
        if (getRetryLimit() == 0) {
            return true;
        }

        return getRetryLimit() - getRetryCount() > 0;
    }
}
