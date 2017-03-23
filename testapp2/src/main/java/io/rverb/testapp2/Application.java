package io.rverb.testapp2;

import io.rverb.feedback.Rverbio;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Rverbio.initialize(this);
    }
}
