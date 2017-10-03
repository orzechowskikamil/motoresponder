package com.medziku.motoresponder.redux.sideeffects;

import android.content.Context;
import com.medziku.motoresponder.redux.Store;
import com.medziku.motoresponder.utils.SensorsUtility;

public class Proximity {

    private SensorsUtility sensorsUtility;
    private Store store;

    public Proximity(Store store, Context context) {
        this.store = store;
        this.sensorsUtility = new SensorsUtility(context);
    }

    public void start() {
        this.sensorsUtility.registerSensors();

    }

    public void stop() {
        this.sensorsUtility.unregisterSensors();
    }

}
