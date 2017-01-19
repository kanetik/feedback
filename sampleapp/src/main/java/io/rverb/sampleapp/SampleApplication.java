package io.rverb.sampleapp;

import io.rverb.feedback.DebugApplication;
import io.rverb.feedback.Rverbio;

public class SampleApplication extends DebugApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Rverbio.initialize(this);
        Rverbio.getInstance().addContextDataItem("Test 2", "This happened at Application#onCreate");
    }
}
