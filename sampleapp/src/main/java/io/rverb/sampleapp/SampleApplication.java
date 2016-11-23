package io.rverb.sampleapp;

import io.rverb.feedback.DebugApplication;
import io.rverb.feedback.Rverbio;

public class SampleApplication extends DebugApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Rverbio.initialize(this);
    }
}
