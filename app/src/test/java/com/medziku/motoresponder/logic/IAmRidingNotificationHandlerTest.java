package com.medziku.motoresponder.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.IntentsUtility;
import com.medziku.motoresponder.utils.NotificationUtility;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class IAmRidingNotificationHandlerTest extends TestCase {

    IAmRidingNotificationHandler iAmRidingNotificationHandler;
    private Context context;
    private Settings settings;
    private NotificationUtility notificationUtility;
    private IntentsUtility intentsUtility;

    @Before
    public void setUp() {
        this.context = mock(Context.class);
        this.settings = mock(Settings.class);
        this.notificationUtility = mock(NotificationUtility.class);

        this.intentsUtility = mock(IntentsUtility.class);

        this.iAmRidingNotificationHandler = new IAmRidingNotificationHandler(this.context, this.settings, this.notificationUtility,this.intentsUtility);
    }

    @Test
    public void testHandlingNotification() {
        when(this.settings.isResponderEnabled()).thenReturn(true);
        when(this.settings.isRidingAssumed()).thenReturn(true);
        this.iAmRidingNotificationHandler.handleNotification();

        verify(this.context, times(1)).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));
        verify(this.settings, times(1)).listenToSettingChange(anyString(), any(Predicate.class));

        verify(this.notificationUtility, times(1)).showOngoingNotification(anyString(), anyString(), anyString(), anyInt(), anyString());

        this.iAmRidingNotificationHandler.stopHandlingNotification();


        verify(this.context, times(1)).unregisterReceiver(any(BroadcastReceiver.class));
        verify(this.settings, times(1)).stopListeningToSetting(anyString(), any(Predicate.class));

    }

}