package com.medziku.motoresponder.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.medziku.motoresponder.Responder;
import com.medziku.motoresponder.callbacks.CallCallback;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import com.medziku.motoresponder.utils.SensorsUtility;

/**
 * Created by medziku on 22.09.15.
 */
public class BackgroundService extends Service {

    public static final int DARK_VALUE = 3;

    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;

    private Sensor proximitySensor;
    private Sensor lightSensor;

    private boolean sensorListenersRegistered = false;

    private float currentProximity;
    private float lightValue;

    //from activity

    private LocationUtility locationUtility;
    private SMSUtility smsUtility;

    private CallsUtility callsUtility;

    private Responder responder;
    private SensorsUtility sensorsUtility;
    private LockStateUtility lockStateUtility;

    //end from activity



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSensorUpdates();//TODO powinna byc takze mozliwosc wyrejestrowania w on command started dla konkretnej komendy
        Log.d("BackgroundService", "destroed");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("BackgroundService", "created");

        //from activity

        this.locationUtility = new LocationUtility(this);
        this.smsUtility = new SMSUtility(this);
        this.callsUtility = new CallsUtility(this);
        this.sensorsUtility = new SensorsUtility(this);
        this.lockStateUtility = new LockStateUtility(this);

        this.responder = new Responder(this, this.locationUtility, this.sensorsUtility, this.lockStateUtility);

        this.smsUtility.listenForSMS(new SMSReceivedCallback() {
            @Override
            public void onSMSReceived(String phoneNumber, String message) {
                BackgroundService.this.onSMSReceived(phoneNumber, message);
            }
        });

        this.callsUtility.listenForCalls(new CallCallback() {
            @Override
            public void onCall(String phoneNumber) {
                BackgroundService.this.onCallReceived(phoneNumber);
            }
        });

        //end from activity



        this.sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                // TODO Auto-generated method stub

                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    BackgroundService.this.setCurrentProximity(event.values[0]);
                }

                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    BackgroundService.this.setLightValue(event.values[0]);
                }
            }
        };

        registerSensorUpdates();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }



    //////////////

    private void setCurrentProximity(float currentProximity) {
        this.currentProximity = currentProximity;
        Log.d("proximity", Float.toString(currentProximity));//TODO zmienic logger na Slf4j
    }

    private void setLightValue(float lightValue) {
        this.lightValue = lightValue;
        Log.d("light", Float.toString(lightValue));
    }



    public boolean isProxime() {
        return this.currentProximity > (this.proximitySensor.getMaximumRange() / 2);
    }

    public boolean isLightOutside() {
        return this.lightValue < this.DARK_VALUE;
    }

    public void registerSensorUpdates() {
        if (this.sensorListenersRegistered) {
            return;
        }

        this.proximitySensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (this.proximitySensor != null) {
//            String name = this.proximitySensor.getName();
//            float maximumRange = this.proximitySensor.getMaximumRange();
            this.sensorManager.registerListener(this.sensorEventListener, this.proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        this.lightSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (this.lightSensor != null) {
            this.sensorManager.registerListener(this.sensorEventListener, this.lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        this.sensorListenersRegistered = true;
    }

    private void unregisterSensorUpdates() {
        if (!this.sensorListenersRegistered) {
            return;
        }
        this.sensorManager.unregisterListener(this.sensorEventListener);

        this.sensorListenersRegistered = false;
    }


    //from activity

    private void onCallReceived(String phoneNumber) {
        this.responder.onUnAnsweredCallReceived(phoneNumber);
    }

    private void onSMSReceived(String phoneNumber, String message) {
        //this.showToast("SMS arrived! Phone number: " + phoneNumber + ", message: " + message);//TODO change to notification


        this.responder.onSMSReceived(phoneNumber);
    }

    //end from activity

}
