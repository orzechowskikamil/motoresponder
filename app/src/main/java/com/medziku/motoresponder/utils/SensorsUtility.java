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
    private boolean isListening;
    private boolean isProxime;

    /**
     * Default constructor for testing, do not use normally
     */
    public SensorsUtility() {
    }

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

        this.log("isProxime is now " + this.isProxime + "");
    }


    public boolean isProxime() {
        return this.isProxime;
    }


    public void unregisterSensors() {
        if (this.isListening == false) {
            return;
        }
        this.isListening = false;
        this.sensorManager.unregisterListener(this.sensorEventListener);
    }

    /**
     * Method for logging instead of static to easier mocking in unit tests.
     * @param msg
     */
    protected void log(String msg) {
        Log.d("SensorsUtility", msg);
    }

}
