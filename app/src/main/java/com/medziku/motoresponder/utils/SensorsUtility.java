package com.medziku.motoresponder.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * Reports about Light and Proximity current values.
 */
public class SensorsUtility {
    // TODO K. Orzechowski: try to make this class not listener, but reading current value from system. Issue #53

    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;

    private Sensor proximitySensor;

    private float currentProximity;
    private boolean isListening;
    private boolean isProxime;

    public SensorsUtility(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.proximitySensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    public void registerSensors() {
        if (this.isListening == true) {
            return;
        }

        this.isListening = true;

        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    SensorsUtility.this.setCurrentProximity(event.values[0]);
                }

            }
        };


        if (this.proximitySensor != null) {
            this.sensorManager.registerListener(
                    this.sensorEventListener,
                    this.proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }

    }

    private void setCurrentProximity(float currentProximity) {
        this.currentProximity = currentProximity;
        this.isProxime = this.currentProximity != this.proximitySensor.getMaximumRange();
    }


    public boolean isProxime() {
        if (this.isListening == false) {
            throw new RuntimeException("You should call registerSensors method first");
        }
        return this.isProxime;
    }


    public void unregisterSensors() {
        if (this.isListening == false) {
            return;
        }
        this.isListening = false;
        this.sensorManager.unregisterListener(this.sensorEventListener);
    }


}
