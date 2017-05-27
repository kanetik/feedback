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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class RverbioUtilsTest {
    private static final String END_USER_ID = UUID.randomUUID().toString();

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
        Mockito.when(_context.getPackageName()).thenReturn(END_USER_ID);
    }

    @After
    public void cleanUp() throws Exception {

    }

    @Test
    public void canGetEndUserId() throws Exception {
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(_context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn(END_USER_ID);

        assertEquals(RverbioUtils.getEndUser(_context).endUserId, END_USER_ID);
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
