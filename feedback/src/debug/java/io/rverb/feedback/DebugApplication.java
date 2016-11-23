package io.rverb.feedback;

import com.facebook.stetho.Stetho;

public class DebugApplication extends FeedbackApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
}
