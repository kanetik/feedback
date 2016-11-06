package io.rverb.feedback;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HelpUtilsTest {
    @Test
    public void getSupportId() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        UUID supportId = UUID.fromString(HelpUtils.getSupportId(appContext));

        assertNotNull(supportId);
        assertTrue(supportId instanceof UUID);
    }
}
