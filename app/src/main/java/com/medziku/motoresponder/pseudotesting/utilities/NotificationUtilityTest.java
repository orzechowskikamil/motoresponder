package com.medziku.motoresponder.pseudotesting.utilities;

import android.content.Context;
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
        Log.d(TAG, "Starting showingToast test");

        this.notificationUtility.showToast("Test toast");
        Log.d(TAG, "Verify if \"Test toast\" is shown!");
    }


    public void testOfShowingAndHidingOngoingNotification() {
        this.setUp();
        Log.d(TAG, "Starting ShowingAndHiding ongoing notification test");


        this.notificationUtility.showOngoingNotification("hideable test notification title", "hideable test notification content", "hideable test notification info");
        Log.d(TAG, "verify if test notification is shown");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.notificationUtility.hideOngoingNotification();

        Log.d(TAG, "Verify if notification dissapears!");
    }

    private void setUp() {
        this.notificationUtility = new NotificationUtility(this.context);
    }
}
