package io.rverb.testapp2;

import io.rverb.feedback.Rverbio;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Rverbio.initialize(this, "fbb9a83c-da3a-4603-b2c2-9d3104c11b6b");

        Rverbio.getInstance().addContextDataItem("Test 1", "Test 1 Value");
        Rverbio.getInstance().addContextDataItem("Test 2", "Test 2 Value");
        Rverbio.getInstance().addContextDataItem("Test 3", "Test 3 Value");
        Rverbio.getInstance().addContextDataItem("Test 4", "Test 4 Value");
        Rverbio.getInstance().addContextDataItem("Test 5", "Test 5 Value");
        Rverbio.getInstance().addContextDataItem("Test 6", "Test 6 Value");
        Rverbio.getInstance().addContextDataItem("Test 7", "Test 7 Value");
        Rverbio.getInstance().addContextDataItem("Test 8", "Test 8 Value");
    }
}
