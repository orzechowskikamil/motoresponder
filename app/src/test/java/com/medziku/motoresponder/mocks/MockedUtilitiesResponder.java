package com.medziku.motoresponder.mocks;

import android.content.Context;
import com.medziku.motoresponder.logic.Responder;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.*;

import static org.mockito.Mockito.mock;

public class MockedUtilitiesResponder extends Responder {

    public Settings settingsMock;
    public SharedPreferencesUtility sharedPreferenceUtilityMock;
    public LockStateUtilityMock mockLockStateUtility;
    public SMSUtilityMock smsUtilityMock;
    public CallsUtilityMock callsUtilityMock;
    public ContactsUtilityMock contactsUtilityMock;
    public LocationUtilityMock locationUtilityMock;
    public LockStateUtilityMock lockStateUtilityMock;
    public NotificationUtility notificationUtilityMock;
    public WiFiUtility wiFiUtilityMock;
    public SensorsUtility sensorsUtilityMock;
    public MotionUtilityMock motionUtilityMock;


    public MockedUtilitiesResponder(Context context) {
        super(context);
    }

    @Override
    protected Settings createSettings() {
        this.settingsMock = mock(Settings.class);
        return this.settingsMock;
    }


    @Override
    protected void createUtilities() {
        try {
            this.sharedPreferencesUtility = mock(SharedPreferencesUtility.class);
            this.sharedPreferenceUtilityMock = this.sharedPreferencesUtility;

            this.mockLockStateUtility = new LockStateUtilityMock();
            this.lockStateUtility = this.mockLockStateUtility.mock;

            this.smsUtilityMock = new SMSUtilityMock();
            this.smsUtility = this.smsUtilityMock.mock;

            this.callsUtilityMock = new CallsUtilityMock();
            this.callsUtility = this.callsUtilityMock.mock;

            this.contactsUtilityMock = new ContactsUtilityMock();
            this.contactsUtility = this.contactsUtilityMock.mock;

            this.locationUtilityMock = new LocationUtilityMock();
            this.locationUtility = this.locationUtilityMock.mock;

            this.lockStateUtilityMock = new LockStateUtilityMock();
            this.lockStateUtility = this.lockStateUtilityMock.mock;

            this.notificationUtilityMock = mock(NotificationUtility.class);
            this.notificationUtility = this.notificationUtilityMock;

            this.wiFiUtilityMock = mock(WiFiUtility.class);
            this.wiFiUtility = this.wiFiUtilityMock;

            this.sensorsUtilityMock = mock(SensorsUtility.class);
            this.sensorsUtility = this.sensorsUtilityMock;

            this.motionUtilityMock =new MotionUtilityMock();
            this.motionUtility = this.motionUtilityMock.mock;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
