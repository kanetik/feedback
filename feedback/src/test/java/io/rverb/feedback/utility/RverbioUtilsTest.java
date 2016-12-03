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

import io.rverb.feedback.RverbioUtils;
import io.rverb.feedback.model.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class RverbioUtilsTest {
    private static final String SUPPORT_ID = UUID.randomUUID().toString();
    private static final Session SESSION_DATA = new Session("test", "test");

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
        final SharedPreferences.Editor sharedPreferencesEditor = Mockito.mock(SharedPreferences.Editor.class);

        Mockito.when(_context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("");

        Mockito.when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        Mockito.when(sharedPreferencesEditor.putString(anyString(), anyString())).thenReturn(sharedPreferencesEditor);

        boolean success = RverbioUtils.initializeSupportId(_context);
        assertTrue(success);
    }

    @Test
    public void doesNotReinitializeSupportIdThatAlreadyExists() throws Exception {
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        final SharedPreferences.Editor sharedPreferencesEditor = Mockito.mock(SharedPreferences.Editor.class);

        Mockito.when(_context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn(SUPPORT_ID);

        Mockito.when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        Mockito.when(sharedPreferencesEditor.putString(anyString(), anyString())).thenReturn(sharedPreferencesEditor);

        boolean success = RverbioUtils.initializeSupportId(_context);
        assertFalse(success);
    }

    @Test
    public void canGetSupportIdAfterInit() throws Exception {
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(_context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn(SUPPORT_ID);

        assertEquals(RverbioUtils.getSupportId(_context), SUPPORT_ID);
    }

    @Test(expected=IllegalStateException.class)
    public void throwsErrorGettingSupportIdBeforeInit() throws Exception {
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(_context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("");

        RverbioUtils.getSupportId(_context);
    }

    @Test
    public void canWriteObjectToDisk() throws Exception {
        assertNotNull(RverbioUtils.writeObjectToDisk(_context, SESSION_DATA));
    }

    @Test
    public void emptyStringIsNullOrWhiteSpace() throws Exception {
        assertTrue(RverbioUtils.isNullOrWhiteSpace(""));
    }

    @Test
    public void blankIsNullOrWhiteSpace() throws Exception {
        assertTrue(RverbioUtils.isNullOrWhiteSpace(" "));
    }

    @Test
    public void nullIsNullOrWhiteSpace() throws Exception {
        assertTrue(RverbioUtils.isNullOrWhiteSpace(null));
    }

    @Test
    public void textIsNotNullOrWhiteSpace() throws Exception {
        assertFalse(RverbioUtils.isNullOrWhiteSpace("test"));
    }
}
