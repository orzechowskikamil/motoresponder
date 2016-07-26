package com.medziku.motoresponder.mocks;

import android.location.Location;
import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.LocationUtility;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Future;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationUtilityMock {

    public LocationUtility mock;
    private Location lastRequestedLocation;
    private Location[] locations;
    private int currentLocationIndex;

    public LocationUtilityMock() {
        this.mock = mock(LocationUtility.class);
        this.setupMock();
    }

    public void setMockLocations(Location[] locations) {
        this.locations = locations;
        this.currentLocationIndex = 0;
    }

    public Location createLocation(float speed) {
        Location location = new Location("");
        location.setSpeed(speed);
        return location;
    }

    private void setupMock() {
        when(this.mock.getAccurateLocation(anyFloat(), anyFloat(), anyInt())).thenAnswer(new Answer() {
            public Future<Location> answer(InvocationOnMock invocation) throws Throwable {
                final SettableFuture<Location> value = SettableFuture.create();

                (new Thread() {
                    public void run() {
                        LocationUtilityMock.this.lastRequestedLocation = LocationUtilityMock.this.locations[currentLocationIndex];
                        value.set(lastRequestedLocation);
                        LocationUtilityMock.this.currentLocationIndex++;
                    }
                }).start();

                return value;
            }
        });
    }
}
