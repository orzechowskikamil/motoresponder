package com.medziku.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * Created by Kamil on 2015-09-16.
 */
public class ProximityUtility {

    SensorManager mySensorManager;
    Sensor myProximitySensor;
    private float currentProximity;


    public ProximityUtility(Context context) {
        mySensorManager = (SensorManager) context.getSystemService(
                Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);

        if (myProximitySensor == null) {
            //
        } else {
            String name = myProximitySensor.getName();
            float maximumRange = myProximitySensor.getMaximumRange();
            mySensorManager.registerListener(proximitySensorEventListener,
                    myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    SensorEventListener proximitySensorEventListener
            = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                float value = event.values[0];
                ProximityUtility.this.setCurrentProximity(value);
            }
        }
    };

    public float getCurrentProximity() {
        return this.currentProximity;
    }

    private void setCurrentProximity(float currentProximity) {
        this.currentProximity = currentProximity;
    }
}

