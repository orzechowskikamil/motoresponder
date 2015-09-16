package com.medziku.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Kamil on 2015-09-16.
 */
public class SensorsUtility {

    SensorManager mySensorManager;
    Sensor myProximitySensor;
    private float currentProximity;

    private float lightValue;


    public SensorsUtility(Context context) {
        this.mySensorManager = (SensorManager) context.getSystemService(
                Context.SENSOR_SERVICE);
        this.myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);

        if (this.myProximitySensor == null) {
            //
        } else {
            String name = this.myProximitySensor.getName();
            float maximumRange = this.myProximitySensor.getMaximumRange();
            this.mySensorManager.registerListener(this.sensorEventListener,
                    this.myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }


        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor != null) {

            mySensorManager.registerListener(
                    sensorEventListener,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);


        }


    }

    SensorEventListener sensorEventListener
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
                SensorsUtility.this.setCurrentProximity(value);
            }

            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                SensorsUtility.this.setLightValue(event.values[0]);
            }
        }
    };

    public float getCurrentProximity() {
        return this.currentProximity;
    }

    private void setCurrentProximity(float currentProximity) {
        this.currentProximity = currentProximity;
    }

    public float getLightValue() {
        return lightValue;
    }

    private void setLightValue(float lightValue) {
        this.lightValue = lightValue;
    }
}

