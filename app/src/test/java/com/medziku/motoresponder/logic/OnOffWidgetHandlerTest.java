package com.medziku.motoresponder.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.IntentsUtility;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class OnOffWidgetHandlerTest extends TestCase {

    private OnOffWidgetHandler onOffWidgetHandler;
    private Context context;
    private Settings settings;
    private IntentsUtility intentsUtility;

    @Before
    public void setUp() {
        this.context = mock(Context.class);
        this.settings = mock(Settings.class);
        this.intentsUtility = this.createIntentsUtility();
        this.onOffWidgetHandler = new OnOffWidgetHandler(this.context, this.settings, this.intentsUtility);
    }

    @Test
    public void testListeningToIntent() {
        this.onOffWidgetHandler.handleWidget();
        verify(this.settings, times(2)).listenToSettingChange(anyString(), any(Predicate.class));
        verify(this.context, times(1)).sendBroadcast(any(Intent.class));
        verify(this.context, times(1)).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));

        this.onOffWidgetHandler.stopHandlingWidget();
        verify(this.settings, times(2)).stopListeningToSetting(anyString(), any(Predicate.class));
        verify(this.context, times(2)).sendBroadcast(any(Intent.class));
        verify(this.context, times(1)).unregisterReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void testHandlingWidgetTap() {
        // verifies if handleWidgetTap toogle setting correctly, and only when responder is enabled.

        when(this.settings.isRidingAssumed()).thenReturn(false);
        when(this.settings.isResponderEnabled()).thenReturn(true);

        this.onOffWidgetHandler.handleWidgetTap();

        verify(this.settings, times(1)).setRidingAssumed(true);
        verify(this.settings, times(0)).setRidingAssumed(false);


        when(this.settings.isResponderEnabled()).thenReturn(false);
        this.onOffWidgetHandler.handleWidgetTap();

        verify(this.settings, times(1)).setRidingAssumed(true);
        verify(this.settings, times(0)).setRidingAssumed(false);
    }

    private IntentsUtility createIntentsUtility() {
        IntentsUtility mock = mock(IntentsUtility.class);
        when(mock.createIntent(any(Context.class), any(Class.class))).thenAnswer(new Answer<Intent>() {
            public Intent answer(InvocationOnMock invocation) throws Throwable {
                return mock(Intent.class);
            }
        });
        when(mock.createIntentFilter(anyString())).thenReturn(mock(IntentFilter.class));
        return mock;
    }


}