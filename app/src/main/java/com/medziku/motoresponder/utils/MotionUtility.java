package com.medziku.motoresponder.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

/**
 * Util for getting info about device motion
 */
public class MotionUtility {

    private final Sensor accelerometer;
    public double movementTreshold = 2;
    // TODO K. Orzechowski: it for development, normally it should be like 5-10
    public int eventsNeeded = 5000;
    // if no movement, listener got no events. NO events in five seconds - we assume phone laying still.
    // TODO K. Orzechowski: set up for development, normally it should be 5*1000
    public int gettingAccelerationTimeout = 130 * 1000;
    public int accelerometerDelay = 0;

    private SensorManager sensorManager;

    public MotionUtility(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public Future<Boolean> isDeviceInMotion() {
        // TODO k.orzechowsk add TYPE_GYROSCOPE or TYPE_ROTATION_VECTOR
        final SettableFuture<Boolean> result = SettableFuture.create();

        final SensorEventListener listener = new SensorEventListener() {

            private double accelerationLast = SensorManager.GRAVITY_EARTH;

            private int eventCounter = 0;
            private int xCoord = 0;
            private int yCoord = 1;
            private int zCoord = 2;


            public void onSensorChanged(SensorEvent e) {
                Log.d("motoapp", "MotionUtility: sensorChanged motion event");

                double x = e.values[this.xCoord];
                double y = e.values[this.yCoord];
                double z = e.values[this.zCoord];

                double accelerationCurrent = Math.sqrt(x * x + y * y + z * z);
                double delta = this.accelerationLast - accelerationCurrent;

                Log.d("motoapp", "MotionUtility: accelerationCurrent is " + accelerationCurrent);

                if (delta > MotionUtility.this.movementTreshold) {
                    eventCounter++;
                }

                if (eventCounter > MotionUtility.this.eventsNeeded) {
                    MotionUtility.this.sensorManager.unregisterListener(this);
                    result.set(true);
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("motoapp", "MotionUtility: unregistered motion listener");
                MotionUtility.this.sensorManager.unregisterListener(listener);
                result.set(false);
            }
        }, this.gettingAccelerationTimeout);

        this.sensorManager.registerListener(listener, this.accelerometer, this.accelerometerDelay);
        Log.d("motoapp", "MotionUtility: registered listener");

        return result;
    }
}
