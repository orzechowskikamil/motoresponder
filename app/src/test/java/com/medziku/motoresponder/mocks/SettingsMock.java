package com.medziku.motoresponder.mocks;

import com.medziku.motoresponder.logic.Settings;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is settings class, with some fields already mocked, in order to reduce testing effort.
 * Settings are pretty standard.
 * <p/>
 * Standard settings can be easily overriden like this
 *
 * @example SettingsMock settings = new SettingsMock();
 * when(mock.mock.someMethod()).thenReturn("new value");
 */
public class SettingsMock {

    public final Settings mock;
    public String SUMMARY_NOTIFICATION_BIG_TEXT = "summary notification big text %recipient%";
    public String SUMMARY_NOTIFICATION_TITLE_TEXT = "summary notification title";
    public String SUMMARY_NOTIFICATION_SHORT_TEXT = "summary notification short text";

    public SettingsMock() {
        this.mock = mock(Settings.class);
        this.setupMock();
    }


    private void setupMock() {
        this.mock.SUMMARY_NOTIFICATION_BIG_TEXT_RES_ID = 1;
        this.mock.SUMMARY_NOTIFICATION_TITLE_TEXT_RES_ID = 2;
        this.mock.SUMMARY_NOTIFICATION_SHORT_TEXT_RES_ID = 3;
        this.mock.ONGOING_NOTIFICATION_TITLE_TEXT_RES_ID = 4;
        this.mock.ONGOING_NOTIFICATION_BIG_TEXT_RES_ID = 5;

        when(this.mock.isResponderEnabled()).thenReturn(true);
        when(this.mock.getSureRidingSpeedKmh()).thenReturn(30);
        when(this.mock.getQuickSpeedCheckDurationSeconds()).thenReturn(30);
        when(this.mock.getRequiredAccuracyMeters()).thenReturn(30);
        when(this.mock.getMaximumStayingStillSpeedKmh()).thenReturn(2);
        when(this.mock.getLongSpeedCheckDurationSeconds()).thenReturn(240);

        when(this.mock.getStringFromRes(this.mock.SUMMARY_NOTIFICATION_BIG_TEXT_RES_ID)).thenReturn(SUMMARY_NOTIFICATION_BIG_TEXT);
        when(this.mock.getStringFromRes(this.mock.SUMMARY_NOTIFICATION_SHORT_TEXT_RES_ID)).thenReturn(SUMMARY_NOTIFICATION_SHORT_TEXT);
        when(this.mock.getStringFromRes(this.mock.SUMMARY_NOTIFICATION_TITLE_TEXT_RES_ID)).thenReturn(SUMMARY_NOTIFICATION_TITLE_TEXT);
        when(this.mock.getStringFromRes(this.mock.ONGOING_NOTIFICATION_TITLE_TEXT_RES_ID)).thenReturn("ongoing");
        when(this.mock.getStringFromRes(this.mock.ONGOING_NOTIFICATION_BIG_TEXT_RES_ID)).thenReturn("ongoing");
//    when(this.mock.()).thenReturn();
//    when(this.mock.()).thenReturn();
//    when(this.mock.()).thenReturn();


    }
}


