package com.medziku.motoresponder.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Reports about Light and Proximity current values.
 */
public class SensorsUtility {
    // TODO K. Orzechowski: try to make this class not listener, but reading current value from system. Issue #53

    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;

    private Sensor proximitySensor;

    private float currentProximity;

    public SensorsUtility(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.proximitySensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.registerSensors();
    }

    private void registerSensors() {

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
    }


    public boolean isProxime() {
        // maximum is away, and not maximum is proxime.
        // TODO K. Orzechowski: add self teaching mechanism of storing minimum and maximum.
        // TODO K. Orzechowski: Or think about reliable mechanism to read min and max. Issue #61
        return this.currentProximity != this.proximitySensor.getMaximumRange();
    }


    public void unregisterSensors() {
        this.sensorManager.unregisterListener(this.sensorEventListener);
    }


}
