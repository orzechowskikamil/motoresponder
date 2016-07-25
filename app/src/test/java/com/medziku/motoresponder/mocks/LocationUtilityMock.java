package com.medziku.motoresponder.mocks;

import android.location.Location;
import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.LocationUtility;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Future;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationUtilityMock {

    public LocationUtility mock;
    private Location lastRequestedLocation;
    private Object[] locations;
    private int currentLocationIndex;

    public LocationUtilityMock() {
        this.mock = mock(LocationUtility.class);
        this.setupMock();
    }


    /**
     * Set new set of mock locations. Locations will be returned in order, when getAccurateLocation will be called.
     * For first call, first from set will be returned, for second will be returned second, etc.
     * <p/>
     * Calling it again will reset previous set and replace it with new one.
     */
    public void setMockLocations(Object[] locations) {
        this.locations = locations;
        this.currentLocationIndex = 0;
    }


    /**
     * Create mocked location
     *
     * @param speedMs          speed in meters/second, will be returned when location.getSpeed() will be called
     * @param distanceToMeters will be returned when location.distanceTo(otherLocation) will be called. Warning -
     *                         it doesn't calculate anything, it just report this plain value for any other location,
     *                         so make sure your data is consise.
     */
    public Location createMockLocation(float speedMs, float distanceToMeters) {
        Location location = mock(Location.class);
        when(location.getSpeed()).thenReturn(speedMs);
        when(location.distanceTo(any(Location.class))).thenReturn(distanceToMeters);
        return location;
    }

    public Location createMockLocation(float speedMs) {
        return this.createMockLocation(speedMs, 0);
    }


    private void setupMock() {
        when(this.mock.getAccurateLocation(anyFloat(), anyFloat(), anyInt())).thenAnswer(new Answer() {
            public Future<Location> answer(InvocationOnMock invocation) throws Throwable {
                final SettableFuture<Location> value = SettableFuture.create();

                (new Thread() {
                    public void run() {
                        Object locationObj = LocationUtilityMock.this.locations[currentLocationIndex];


                        if (locationObj instanceof Location || locationObj == null) {
                            LocationUtilityMock.this.lastRequestedLocation = (Location) locationObj;
                            value.set(lastRequestedLocation);
                        } else if (locationObj instanceof Exception) {
                            value.setException((Exception) locationObj);
                        }
                        LocationUtilityMock.this.currentLocationIndex++;
                    }
                }).start();

                return value;
            }
        });
    }
}
