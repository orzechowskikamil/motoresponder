package com.medziku.motoresponder;


import android.content.Context;
import android.location.*;
import android.os.Bundle;


//interface LocationCityChangedCallback {
//
//    void onLocationCityChange(Location location, String cityName);
//}

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
//
//    public void listenForLocationChanges(LocationChangedCallback locationChangedCallback, boolean shouldReceiveCity) {
//        this.locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                this.minimumTimeBetweenUpdates,
//                this.minimumDistanceBetweenUpdates,
//                new MyLocationListener(locationChangedCallback, shouldReceiveCity)
//        );
//    }
//
//    private LocationListener  listenForLocationChanges(LocationChangedCallback locationChangedCallback, boolean shouldReceiveCity) {
//        LocationListener locationListener = new MyLocationListener(locationChangedCallback, shouldReceiveCity);
//        this.locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                this.minimumTimeBetweenUpdates,
//                this.minimumDistanceBetweenUpdates,
//                locationListener
//        );
//
//        return locationListener;
//    }
//
//    public void listenForLocationChangesOnce(final LocationChangedCallback locationChangedCallback, boolean shouldReceiveCity) {
//        this.listenForLocationChanges(new LocationChangedCallback() {
//
//            public void onLocationChange(Location location, String cityName) {
//                LocationUtility.this.locationManager.removeUpdates();
//                locationChangedCallback.onLocationChange(location, cityName);
//            }
//
//
//            public void onLocationChange(Location location) {
//                locationChangedCallback.onLocationChange(location);
//            }
//        }, shouldReceiveCity);
//    }


    public void listenForLocationOnce(final LocationChangedCallback callback) {

        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                this.minimumTimeBetweenUpdates,
                this.minimumDistanceBetweenUpdates,
                new LocationListener() {
                    public void onLocationChanged(Location loc) {
                        if (loc.getAccuracy() >= 0.68) {
                            callback.onLocationChange(loc);
                            this.unregisterUpdates();
                        }
                    }

                    private void unregisterUpdates() {
                        LocationUtility.this.locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }

//    private class MyLocationListener implements LocationListener {//responsible for receiving GPS info
//
//        private LocationChangedCallback locationChangedCallback;
//        private LocationCityChangedCallback locationCityChangedCallback;
//        private boolean isTrackingCityEnabled;
//
//        public MyLocationListener(LocationChangedCallback locationChangedCallback) {
//            this(locationChangedCallback, false);
//        }
//
//        public MyLocationListener(LocationChangedCallback locationChangedCallback, boolean isTrackingCityEnabled) {
//            this.locationChangedCallback = locationChangedCallback;
//            this.isTrackingCityEnabled = isTrackingCityEnabled;
//        }
//
//        @Override
//        public void onLocationChanged(Location loc) {
//            if (loc.getAccuracy() >= 0.68) {
//                if (this.isTrackingCityEnabled) {
//                    String cityName = null;
//                    Geocoder gcd = new Geocoder(LocationUtility.this.context, Locale.getDefault());
//                    List<Address> addresses;
//                    try {
//                        addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
//                        if (addresses.size() > 0) {
//                            System.out.println(addresses.get(0).getLocality());
//                        }
//                        cityName = addresses.get(0).getLocality();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    this.locationCityChangedCallback.onLocationCityChange(loc, cityName);
//                } else {
//                    this.locationChangedCallback.onLocationChange(loc);
//                }
//            }
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//    }

}