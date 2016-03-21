package com.medziku.motoresponder.pseudotesting.utilities;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.medziku.motoresponder.utils.LocationUtility;

import java.util.concurrent.Future;

public class LocationUtilityTest {

    public static final String TAG = "LocationUtilityTest";

    private LocationUtility locationUtility;

    private final Context context;

    public LocationUtilityTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.locationUtility = new LocationUtility(this.context);
    }


    public void testOfGettingAccurateLocation() {
        this.setUp();

        Log.d(TAG, "Starting GettingAccurateLocation test");

        Location location = null;

        try {
            Future<Location> accurateLocation = this.locationUtility.getAccurateLocation(-1, 20, 60 * 1000);
            location = accurateLocation.get();
        } catch (Exception e) {
            Log.e(TAG, "Error happened during getting location");
        }

        if (location == null) {
            Log.d(TAG, "Done! Location is timeouted");
            return;
        }

        Log.d(TAG, "Done! Location data: speed=" + location.getSpeed() + " and accuracy=" + location.getAccuracy() + ".");
    }
}
