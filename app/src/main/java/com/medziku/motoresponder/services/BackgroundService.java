package com.medziku.motoresponder.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.medziku.motoresponder.R;
import com.medziku.motoresponder.Responder;
import com.medziku.motoresponder.activity.SettingsActivity;
import com.medziku.motoresponder.callbacks.CallCallback;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.utils.*;

/**
 * Created by medziku on 22.09.15.
 */
public class BackgroundService extends Service {


    private boolean sensorListenersRegistered = false;


    private int notificationId = 0;

    //from activity

    private LocationUtility locationUtility;
    private SMSUtility smsUtility;

    private CallsUtility callsUtility;

    private Responder responder;
    private LockStateUtility lockStateUtility;
    private SensorsUtility sensorsUtility;

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
        hideNotification();
        this.sensorsUtility.unregisterSensors();//TODO powinna byc takze mozliwosc wyrejestrowania w on command started dla konkretnej komendy
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
        this.lockStateUtility = new LockStateUtility(this);
        this.sensorsUtility = new SensorsUtility(this);

        this.responder = new Responder(this, this.locationUtility, this.lockStateUtility, this.sensorsUtility);

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

        showNotification();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }


    //mine

    public void showStupidNotify(String title, String content) {
        this.hideNotification();
        this.showNotification(title, content, "test info");
    }


    private void showNotification() {
        this.showNotification("Test title", "Test content", "test info");
    }

    private void showNotification(String title, String content, String info) {
        Intent resultIntent = new Intent(this, SettingsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SettingsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo(info)
                .setContentIntent(resultPendingIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, notification);
    }

    private void hideNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }


    //from activity

    private void onCallReceived(String phoneNumber) {
        this.responder.onUnAnsweredCallReceived(phoneNumber);
    }

    private void onSMSReceived(String phoneNumber, String message) {
        //this.showToast("SMS arrived! Phone number: " + phoneNumber + ", message: " + message);
        // TODO change to notification


        this.responder.onSMSReceived(phoneNumber);
    }

    //end from activity


}
