package io.rverb.feedback;

import android.content.Context;

import org.junit.Before;
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
}
