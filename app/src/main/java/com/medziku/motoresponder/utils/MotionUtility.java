package com.medziku.motoresponder.utils;

import android.content.Context;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Future;

/**
 * Util for getting info about device motion
 */
public class MotionUtility {
    
    public double movementTreshold = 2;
    public int eventsNeeded=5;
    // if no movement, listener got no events. NO events in two seconds - we assume phone laying still.
    public int gettingAccelerationTimeout = 2*1000;
            
    private SensorManager sensorManager;
    public MotionUtility(Context context) {
        this.sensorManager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public Future<Boolean> isDeviceInMotion() {
        SettableFuture<Boolean> result = SettableFuture.create();

        final SensorEventListener accelerometerSensorListener = new SensorEventListener() {
            
            private double accelerationLast = SensorManager.GRAVITY_EARTH;
            
            private int eventCounter = 0;
            

            public void onSensorChanged(SensorEvent e) {
              double x = e.values[SensorManager.DATA_X];
              double y = e.values[SensorManager.DATA_Y];
              double z = e.values[SensorManager.DATA_Z];
              
              float delta = mAccelCurrent - mAccelLast;
              mAccel = mAccel * 0.9f + delta; // perform low-cut filter
              
              double accelerationCurrent = Math.sqrt((double) (x*x + y*y + z*z));
              double delta = this.accelerationLast - accelerationCurrent;
              
              if (delta>MotionUtility.this.movementTreshold){
                  eventCounter++;
              }
              
              if (eventCounter>MotionUtility.this.eventsNeeded){
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
                Log.d("loc", "location timeout");
                 MotionUtility.this.sensorManager.unregisterListener(listener);
                result.set(false);
            }
        }, this.gettingAccelerationTimeout);
        
        this.sensorManager.registerListener(accelerometerSensorListener, this.accelerometer, SensorManager.SENSOR_DELAY_UI);


        return result;
    }
}
