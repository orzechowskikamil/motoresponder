package com.medziku.motoresponder.integration;

import android.location.Location;
import android.test.mock.MockContext;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.mocks.LocationUtilityMock;
import com.medziku.motoresponder.mocks.MockedUtilitiesResponder;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This is integration test (non-instrumented) where all utility classes (which are dependent on Android APIs, and their
 * purpose is to isolate my logic from unmockable Android APIs) are mocked, so integration testing of logic layer can be done
 * automatically without connecting a device
 */
public class ResponderTest {

    private MockContext context;
    private MockedUtilitiesResponder responder;

    @Before
    public void setUp() throws Exception {
        this.context = new MockContext();
        this.responder = new MockedUtilitiesResponder(this.context);
    }

    @Test
    public void testReactionOnCall() {
        LocationUtilityMock locationMock = this.responder.locationUtilityMock;
        locationMock.setMockLocations(new Location[]{
                locationMock.createLocation(50);
        });

        this.responder.motionUtilityMock.setIsDeviceInMotionResult(true);
        this.responder.


        this.responder.startResponding();
        this.responder.callsUtilityMock.simulateUnansweredCall("777777777");


        verify(this.responder.smsUtilityMock.mock, times(1)).sendSMS(anyString(), anyString(), any(Predicate.class));
    }
    
    




}


