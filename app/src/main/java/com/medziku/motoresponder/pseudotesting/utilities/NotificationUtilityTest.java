package com.medziku.motoresponder.pseudotesting.utilities;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import com.medziku.motoresponder.utils.NotificationUtility;

public class NotificationUtilityTest {
    private static final String TAG = "NotifUtilityRunner";
    private Context context;
    private NotificationUtility notificationUtility;

    public NotificationUtilityTest(Context context) {
        this.context = context;
    }


    public void testOfShowingToast() {
        this.setUp();
        Looper.prepare();
        Log.d(TAG, "Starting showingToast test");

        this.notificationUtility.showToast("Test toast");
        Log.d(TAG, "Verify if \"Test toast\" is shown!");
        Looper.loop();
    }


    public void testOfShowingAndHidingOngoingNotification() {
        this.setUp();
        Log.d(TAG, "Starting ShowingAndHiding ongoing notification test");


        this.notificationUtility.showOngoingNotification("hideable test notification title",
                "hideable test notification content",
                "hideable test notification info");

        Log.d(TAG, "verify if test notification is shown");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.notificationUtility.hideNotification();

        Log.d(TAG, "Verify if notification dissapears!");
    }

    private void setUp() {
        this.notificationUtility = new NotificationUtility(this.context);
    }

    public void testOfShowingNotOngoingNotification() {
        this.setUp();
        Log.d(TAG, "Starting ShowingAndHiding ongoing notification test");


        this.notificationUtility.showNormalNotification("hideable test notification title",
                "hideable test notification content",
                "hideable test notification info");
        Log.d(TAG, "verify if test notification is shown");

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.notificationUtility.hideNotification();

        Log.d(TAG, "Verify if notification dissapears!");

    }

    public void testOfShowingAndHidingBigTextNotification() {
        this.setUp();
        Log.d(TAG, "Starting ShowingAndHiding ongoing notification test");


        this.notificationUtility.showBigTextNotification("hideable test notification title", "summary",
                "Long long long text Long long long text Long long long text Long long long text Long long long text \n" +
                        "Long long long text Long long long text Long long long text Long long long text END OF TEXT");
        Log.d(TAG, "verify if test notification is shown");

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.notificationUtility.hideNotification();

        Log.d(TAG, "Verify if notification dissapears!");

    }
}
