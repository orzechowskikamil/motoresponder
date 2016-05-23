package com.medziku.motoresponder.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WiFiUtility {

    private final Context context;

    public WiFiUtility(Context context) {
        this.context = context;
    }

    public boolean isWifiConnected() {
        return getWiFiManager().getConnectionInfo().getNetworkId() != -1;
    }

    private WifiManager getWiFiManager() {
        return (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }
}
