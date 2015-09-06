package com.medziku.myapplication;


import android.content.Context;
import android.location.*;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

interface LocationChangedCallback {
    void onLocationChange(Location location, String cityName);

    void onLocationChange(Location location);
}

class LocationUtility {

    private Context context;
    private LocationManager locationManager;
    private int minimumTimeBetweenUpdates;
    private int minimumDistanceBetweenUpdates;

    public LocationUtility(Context context, int minimumTimeBetweenUpdates, int minimumDistanceBetweenUpdates) {
        this.context = context;
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.minimumDistanceBetweenUpdates = minimumDistanceBetweenUpdates;
        this.minimumTimeBetweenUpdates = minimumTimeBetweenUpdates;
    }

    public LocationUtility(Context context) {
        this(context, 5000, 10);
    }

    public void listenForLocationChanges(LocationChangedCallback locationChangedCallback, boolean shouldReceiveCity) {
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                this.minimumTimeBetweenUpdates,
                this.minimumDistanceBetweenUpdates,
                new MyLocationListener(locationChangedCallback, shouldReceiveCity)
        );
    }

    private class MyLocationListener implements LocationListener {//responsible for receiving GPS info

        private LocationChangedCallback locationChangedCallback;
        private boolean isTrackingCityEnabled;

        public MyLocationListener(LocationChangedCallback locationChangedCallback) {
            this(locationChangedCallback, false);
        }

        public MyLocationListener(LocationChangedCallback locationChangedCallback, boolean isTrackingCityEnabled) {
            this.locationChangedCallback = locationChangedCallback;
            this.isTrackingCityEnabled = isTrackingCityEnabled;
        }

        @Override
        public void onLocationChanged(Location loc) {
            if (this.isTrackingCityEnabled) {
                String cityName = null;
                Geocoder gcd = new Geocoder(LocationUtility.this.context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        System.out.println(addresses.get(0).getLocality());
                    }
                    cityName = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.locationChangedCallback.onLocationChange(loc, cityName);
            } else {
                this.locationChangedCallback.onLocationChange(loc);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

}