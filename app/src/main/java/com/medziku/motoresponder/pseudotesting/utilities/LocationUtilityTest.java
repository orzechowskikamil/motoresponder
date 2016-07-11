package com.medziku.motoresponder.pseudotesting.utilities;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.medziku.motoresponder.logic.CustomLog;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.concurrent.Future;

public class LocationUtilityTest {

    public static final String TAG = "LocationUtilityTest";
    private final Context context;
    private LocationUtility locationUtility;

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

        Future<Location> future = this.locationUtility.getLastRequestedLocation();
        Location lastKnownLocation = null;

        if (future != null) {
            Log.d(TAG, "ERROR! LastRequestedLocation future should be null on this stage!");
        }

        try {
            Future<Location> accurateLocation = this.locationUtility.getAccurateLocation(-1, 20, 60 * 1000);
            location = accurateLocation.get();

            lastKnownLocation = this.locationUtility.getLastRequestedLocation().get();
        } catch (Exception e) {
            Log.e(TAG, "Error happened during getting location");
        }

        if (location == null) {
            Log.d(TAG, "Done! Location is timeouted");
            return;
        }

        Log.d(TAG, "Done! Location data: speed=" + location.getSpeed() + " and accuracy=" + location.getAccuracy() + ".");

        if (lastKnownLocation != null) {
            Log.d(TAG, "LastKnownLocation should be identical to locationdata, check, speed = "
                    + lastKnownLocation.getSpeed() + " accuracy = " + lastKnownLocation.getAccuracy());
        } else {
            Log.d(TAG, "LastKnownLocation must be not null! ERROR!");
        }

    }

    public void testOfBreakingAccurateLocationProcess() {
        this.setUp();
        Future<Location> future = this.locationUtility.getAccurateLocation(10000, 3, 10000);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.locationUtility.cancelGPSCheck();
        Log.d(TAG, "is done right now? = " + future.isDone());
        Log.d(TAG, "Also check if location icon disappear after two seconds ");
    }
}
