package io.rverb.feedback.utility;


import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class HelpUtilsTest {
    private static final String SUPPORT_ID = UUID.randomUUID().toString();

    @Mock
    Context _context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(_context.getPackageName()).thenReturn(SUPPORT_ID);
    }

    @Test
    public void getSupportId() throws Exception {
        final SharedPreferences sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(_context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn(SUPPORT_ID);

        assertEquals(SUPPORT_ID, HelpUtils.getSupportId(_context));
    }
}
