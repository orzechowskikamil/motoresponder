package com.medziku.motoresponder.utils.utilitiesrunner.runners;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.SensorsUtility;

public class SensorsUtilityRunner {
    private static final String TAG = "SensorsUtilityRunner";
    private Context context;
    private SensorsUtility sensorsUtility;

    public SensorsUtilityRunner(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.sensorsUtility = new SensorsUtility(this.context);
        this.sensorsUtility.registerSensors();
    }


    public void proximitySensorTest(){
        this.setUp();

        Log.d(TAG, "----Active test of proximity sensor----");
        Log.d(TAG, "Place your hand on phone proximity sensor and take it off (or not) but you must provide some input to the sensor");
        Log.d(TAG, "And watch console for proximity updates");
    }
}
