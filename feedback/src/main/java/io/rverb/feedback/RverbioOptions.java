package io.rverb.feedback;

public class RverbioOptions {
    private boolean attachScreenshot = true;

    public RverbioOptions() {
    }

    public boolean isAttachScreenshotEnabled() {
        return attachScreenshot;
    }

    public RverbioOptions setAttachScreenshotEnabled(boolean attachScreenshot) {
        this.attachScreenshot = attachScreenshot;
        return this;
    }
}
