package io.rverb.feedback;

public class RverbioOptions {
    private boolean attachScreenshot;

    public RverbioOptions() {
        attachScreenshot = true;
    }

    public boolean isAttachScreenshotEnabled() {
        return attachScreenshot;
    }

    public RverbioOptions setAttachScreenshotEnabled(boolean attachScreenshot) {
        this.attachScreenshot = attachScreenshot;
        return this;
    }
}
