package io.rverb.feedback.utility;


import android.content.Context;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import io.rverb.feedback.model.SessionData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class RverbioUtilsTest {
    private static final String SUPPORT_ID = UUID.randomUUID().toString();
    private static final String TEMP_PATH = "/temp/file/absolute/path";
    private static final SessionData SESSION_DATA = new SessionData("test", "test");

    @Mock
    Context _context;

    @Mock
    File _file;

    @Mock
    FileOutputStream _fileOutputStream;

    @Mock
    ObjectOutputStream _objectOutputStream;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(_context.getPackageName()).thenReturn(SUPPORT_ID);
    }

    @After
    public void cleanUp() throws Exception {

    }

    @Test
    public void canInitializeSupportId() throws Exception {
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(_context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn(SUPPORT_ID);

        assertEquals(SUPPORT_ID, RverbioUtils.initializeSupportId(_context));
    }

    @Test
    public void canWriteObjectToDisk() throws Exception {
        assertNotNull(RverbioUtils.writeObjectToDisk(_context, RverbioUtils.DATA_TYPE_SESSION, SESSION_DATA));
    }
}
