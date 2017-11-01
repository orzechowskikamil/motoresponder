package com.medziku.motoresponder.redux.sideeffects;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.sideeffects.base.ContextSideEffect;
import trikita.jedux.Action;


public class Proximity extends ContextSideEffect {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener sensorEventListener;

    public void registerSensors() {
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    Proximity.this.dispatchProximityChangedAction(event.values[0]);
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

    public void unregisterSensors() {
        this.sensorManager.unregisterListener(this.sensorEventListener);
    }

    @Override
    protected void afterStart() {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.proximitySensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.registerSensors();
    }

    @Override
    protected void beforeStop() {
        this.unregisterSensors();
    }

    private void dispatchProximityChangedAction(float value) {
        store.dispatch(new Action(Actions.Proximity.PROXIMITY, new Float[]{value, this.proximitySensor.getMaximumRange()}));
    }
}