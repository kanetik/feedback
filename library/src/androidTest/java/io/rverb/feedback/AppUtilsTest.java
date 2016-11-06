package io.rverb.feedback;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppUtilsTest {
    @Test
    public void getPackageName() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.rverb.feedback.test", AppUtils.getPackageName(appContext));
    }

    @Test
    public void getVersionName() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("Unknown", AppUtils.getVersionName(appContext));
    }

    @Test
    public void isDebug() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals(BuildConfig.DEBUG, AppUtils.isDebug(appContext));
    }
}