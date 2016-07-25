package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.mocks.SettingsMock;
import com.medziku.motoresponder.utils.NotificationUtility;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class NotificationFactoryTest extends TestCase {

    private NotificationFactory notificationFactory;
    private NotificationUtility notificationUtility;
    private SettingsMock settings;

    @Before
    public void setUp() {
        this.notificationUtility = mock(NotificationUtility.class);
        this.settings = new SettingsMock();
        this.notificationFactory = new NotificationFactory(this.notificationUtility, this.settings.mock);
    }

    @Test
    public void testOfDisplayingSummaryNotification() {
        String CONTACT_NAME = "test contact";
        when(this.settings.mock.isShowingPendingNotificationEnabled()).thenReturn(true);

        this.notificationFactory.showSummaryNotification(CONTACT_NAME);

        verify(this.notificationUtility, times(1)).showBigTextNotification(anyString(), anyString(), contains(CONTACT_NAME));
    }


}