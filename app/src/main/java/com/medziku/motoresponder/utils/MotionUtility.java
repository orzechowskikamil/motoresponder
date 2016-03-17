package com.medziku.motoresponder.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

/**
 * Util for getting info about device motion
 */
public class MotionUtility {

    private Sensor linearAccelerometer;

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
    private PowerManager powerManager;

    public MotionUtility(Context context) {
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.linearAccelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }


    /**
     * Reports if device is in motion or not.
     *
     * @return
     * @throws AccelerometerNotAvailableException When device screen is off and utility can't properly measure movement
     */
    public Future<Boolean> isDeviceInMotion() throws AccelerometerNotAvailableException {
        if (this.isDeviceScreenTurnedOff()) {
            throw new AccelerometerNotAvailableException();
        }


        final SettableFuture<Boolean> result = SettableFuture.create();

        final SensorEventListener listener = new SensorEventListener() {

            private boolean firstEventAlreadyHappened = false;
            private double accelerationLast = 0;

            private int eventCounter = 0;
            private int xCoord = 0;
            private int yCoord = 1;
            private int zCoord = 2;


            public void onSensorChanged(SensorEvent e) {
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

                if (delta > MotionUtility.this.accelerationDeltaTresholdForMovement) {
                    eventCounter++;
                }

                if (eventCounter > MotionUtility.this.aboveTresholdEventsNeededToAssumeMovement) {
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
                MotionUtility.this.sensorManager.unregisterListener(listener);
                result.set(false);
            }
        }, this.measuringMovementTimeout);

        this.sensorManager.registerListener(listener, this.linearAccelerometer, this.accelerometerDelayUs);

        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private boolean isDeviceScreenTurnedOff() {
        return !this.powerManager.isInteractive();
    }

}

