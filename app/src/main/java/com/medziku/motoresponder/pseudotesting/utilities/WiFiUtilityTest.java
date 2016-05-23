package com.medziku.motoresponder.pseudotesting.utilities;

import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.WiFiUtility;

public class WiFiUtilityTest {

    private static final String TAG = "WifiUtilityTest";
    private Context context;
    private WiFiUtility wiFiUtility;

    public WiFiUtilityTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.wiFiUtility = new WiFiUtility(this.context);
    }


    public void wifiSensorTest() {
        this.setUp();

        Log.d(TAG, "----Active test of wifi connection available----");

        Log.d(TAG, "in current time WifiUtility.isWifiConnected() = " + this.wiFiUtility.isWifiConnected());

    }
}
