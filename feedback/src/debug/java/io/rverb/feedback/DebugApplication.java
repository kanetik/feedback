package io.rverb.feedback;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class DebugApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
}
