package io.rverb.feedback;

import android.support.annotation.Keep;

@Keep
public class RverbioOptions {
    private boolean attachScreenshotByDefault;
    private boolean debugMode;

    public RverbioOptions() {
        attachScreenshotByDefault = true;
        debugMode = false;
    }

    public boolean attachScreenshotByDefault() {
        return attachScreenshotByDefault;
    }

    public RverbioOptions setAttachScreenshotEnabled(boolean attachScreenshot) {
        this.attachScreenshotByDefault = attachScreenshot;
        return this;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public RverbioOptions setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }
}
