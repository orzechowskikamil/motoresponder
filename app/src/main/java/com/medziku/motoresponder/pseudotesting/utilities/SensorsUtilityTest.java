package com.medziku.motoresponder.pseudotesting.utilities;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.SensorsUtility;

public class SensorsUtilityTest {
    private static final String TAG = "SensorsUtilityTest";
    private Context context;
    private SensorsUtility sensorsUtility;

    public SensorsUtilityTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.sensorsUtility = new SensorsUtility(this.context);
        this.sensorsUtility.registerSensors();
    }


    public void proximitySensorTest() {
        this.setUp();

        Log.d(TAG, "----Active test of proximity sensor----");
        Log.d(TAG, "Place your hand on phone proximity sensor and take it off (or not) but you must provide some input to the sensor, and wait 3s");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "And now grabbing the result!");
        Log.d(TAG, "in current time SensorsUtility.isProxime()=" + this.sensorsUtility.isProxime());

    }
}
