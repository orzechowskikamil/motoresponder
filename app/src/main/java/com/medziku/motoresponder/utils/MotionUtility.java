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

    private final Sensor linearAccelerometer;

    /**
     * for TYPE_LINEAR_ACCELERATION linearAccelerometer, differences between accelerations of laying still
     * phone shouldn't be bigger than 0.2
     */
    public double accelerationDeltaTresholdForMovement = 0.2;

    /**
     * Events with acceleration delta bigger than accelerationDeltaTresholdForMovement to assume that phone is moving,
     * user is riding.
     */
    public int aboveTresholdEventsNeededToAssumeMovement = 6;

    /**
     * App wait for this time (milliseconds) for aboveTresholdEventsNeededToAssumeMovement amounts of events before
     * it assume that there is no movement of device
     */
    public int measuringMovementTimeout = 10 * 1000;

    /**
     * It's in microseconds! 10^-6 of second!
     */
    public int accelerometerDelayUs = 300 * 1000;

    private SensorManager sensorManager;

    public MotionUtility(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.linearAccelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public Future<Boolean> isDeviceInMotion() {
        final SettableFuture<Boolean> result = SettableFuture.create();

        final SensorEventListener listener = new SensorEventListener() {

            private boolean firstEventAlreadyHappened = false;
            private double accelerationLast = 0;

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

                if (firstEventAlreadyHappened == false) {
                    this.accelerationLast = accelerationCurrent;
                    this.firstEventAlreadyHappened = true;
                    return;
                }

                double delta = this.accelerationLast - accelerationCurrent;


                Log.d("motoapp", "MotionUtility: delta is " + delta + ", acc cur: " + accelerationCurrent);

                if (delta > MotionUtility.this.accelerationDeltaTresholdForMovement) {
                    eventCounter++;
                    Log.d("motoapp", "MotionUtility: delta overreached");
                }

                if (eventCounter > MotionUtility.this.aboveTresholdEventsNeededToAssumeMovement) {
                    Log.d("motoapp", "MotionUtility: enough events captured, assuming motion");
                    MotionUtility.this.sensorManager.unregisterListener(this);
                    result.set(true);
                }

                this.accelerationLast = accelerationCurrent;
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
        }, this.measuringMovementTimeout);

        this.sensorManager.registerListener(listener, this.linearAccelerometer, this.accelerometerDelayUs);
        Log.d("motoapp", "MotionUtility: registered listener");

        return result;
    }
}
