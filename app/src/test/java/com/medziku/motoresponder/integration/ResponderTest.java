
package com.medziku.motoresponder.logic;

import android.content.Context;
import android.location.Location;
import android.test.mock.MockContext;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


/**
 * This is integration test (non-instrumented) where all utility classes (which are dependent on Android APIs, and their 
 * purpose is to isolate my logic from unmockable Android APIs) are mocked, so integration testing of logic layer can be done 
 * automatically without connecting a device
 */
public class ResponderIntegrationTest {

    private MockedUtilitiesResponder responder;
    private MockContext context;

    @Before
    public void setUp() throws Exception {
        this.context = new MockContext();
        this.responder = new MockedUtilitiesResponder(this.context);

    }

    @Test
    public void testReactionOnCall() {
        this.responder.startResponding();
    }


}


