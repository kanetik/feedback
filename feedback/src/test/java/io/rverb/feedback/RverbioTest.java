package io.rverb.feedback;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RverbioTest {
    @Mock
    Context _context;

    @Mock
    Rverbio _rv;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void canSendHelp() throws Exception {
        Rverbio._initialized = true;
        _rv.sendHelp(_context);
    }
}
