package io.rverb.feedback.utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AppUtilsTest {
    private static final String PACKAGE = UUID.randomUUID().toString();

    @Mock
    Context _context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(_context.getPackageName()).thenReturn(PACKAGE);
    }

    @Test
    public void canGetPackageName() throws Exception {
        assertEquals(PACKAGE, AppUtils.getPackageName(_context));

        Mockito.verify(_context).getPackageName();
    }

    @Test
    public void canGetVersionName() throws Exception {
        final PackageManager manager = Mockito.mock(PackageManager.class);

        final PackageInfo info = Mockito.mock(PackageInfo.class);
        info.versionName = "1";

        Mockito.when(_context.getPackageManager()).thenReturn(manager);
        Mockito.when(manager.getPackageInfo(PACKAGE, 0)).thenReturn(info);

        String versionName = AppUtils.getVersionName(_context);

        assertEquals("1", versionName);

        Mockito.verify(_context).getPackageManager();
        Mockito.verify(_context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, 0);
    }

    @Test
    public void canGetVersionCode() throws Exception {
        final PackageManager manager = Mockito.mock(PackageManager.class);

        final PackageInfo info = Mockito.mock(PackageInfo.class);
        info.versionCode = 1;

        Mockito.when(_context.getPackageManager()).thenReturn(manager);
        Mockito.when(manager.getPackageInfo(PACKAGE, 0)).thenReturn(info);

        Integer versionCode = AppUtils.getVersionCode(_context);

        assertNotNull(versionCode);
        assertEquals(Integer.valueOf(1), versionCode);

        Mockito.verify(_context).getPackageManager();
        Mockito.verify(_context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, 0);
    }

    @Test
    public void canGetDebuggable() throws Exception {
        final ApplicationInfo applicationInfo = Mockito.mock(ApplicationInfo.class);
        applicationInfo.flags = ApplicationInfo.FLAG_DEBUGGABLE;

        Mockito.when(_context.getApplicationInfo()).thenReturn(applicationInfo);
        assertTrue(AppUtils.isDebug(_context));
    }

    @Test
    public void canGetNotDebuggable() throws Exception {
        final ApplicationInfo applicationInfo = Mockito.mock(ApplicationInfo.class);

        Mockito.when(_context.getApplicationInfo()).thenReturn(applicationInfo);
        assertFalse(AppUtils.isDebug(_context));
    }
}