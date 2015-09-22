package com.medziku.motoresponder.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Kamil on 2015-09-16.
 */

public class SensorsUtility {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Sensor lightSensor;
    private int darkValue = 3;
    private float currentProximity;
    private boolean sensorListenersRegistered = false;

    private float lightValue;

    private SensorEventListener sensorEventListener;


    public SensorsUtility(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub

                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    SensorsUtility.this.setCurrentProximity(event.values[0]);
                }

                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    SensorsUtility.this.setLightValue(event.values[0]);
                }
            }
        };
    }

    public void registerSensorUpdates() {
        if (this.sensorListenersRegistered) {
            return;
        }

        this.proximitySensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (this.proximitySensor != null) {
//            String name = this.proximitySensor.getName();
//            float maximumRange = this.proximitySensor.getMaximumRange();
            this.sensorManager.registerListener(this.sensorEventListener, this.proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        this.lightSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (this.lightSensor != null) {
            this.sensorManager.registerListener(this.sensorEventListener, this.lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        this.sensorListenersRegistered = true;
    }


    private void unregisterSensorUpdates() {
        if (!this.sensorListenersRegistered) {
            return;
        }
        this.sensorManager.unregisterListener(this.sensorEventListener);

        this.sensorListenersRegistered = false;
    }


    private float getCurrentProximity() {
        return this.currentProximity;
    }

    public boolean isProxime() {
        return this.getCurrentProximity() > (this.proximitySensor.getMaximumRange() / 2);
    }

    private void setCurrentProximity(float currentProximity) {
        this.currentProximity = currentProximity;
        Log.d("proximity", Float.toString(currentProximity));
    }

    private float getLightValue() {
        return lightValue;
    }

    public boolean isLightOutside() {
        return this.getLightValue() < this.darkValue;
    }

    private void setLightValue(float lightValue) {
        this.lightValue = lightValue;
        Log.d("light", Float.toString(lightValue));
    }
}

