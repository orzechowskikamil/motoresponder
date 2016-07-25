package com.medziku.motoresponder.logic;

import android.content.Context;
import android.location.Location;
import android.test.mock.MockContext;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class ResponderIntegrationTest {

    private MockedUtilitiesResponder responder;
    private MockContext context;

    @Before
    public void setUp() throws Exception {
        this.context = new MockContext();
        this.responder = new MockedUtilitiesResponder(this.context);

    }

    @Test
    public void testReactionOnCall() {
        this.responder.startResponding();
    }


}


class MockedUtilitiesResponder extends Responder {

    private Settings mockSettings;
    private SharedPreferencesUtility mockSharedPreferencesUtility;
    private LockStateUtility mocklockStateUtility;
    private SMSUtility mockSmsUtility;
    private NotificationUtility mockNotificationUtility;
    private CallsUtility mockCallsUtility;
    private LocationUtility mockLocationUtility;
    private ContactsUtility mockContactsUtility;
    private SensorsUtility mockSensorsUtility;
    private MotionUtility mockMotionUtility;
    private WiFiUtility mockWiFiUtility;

    public MockedUtilitiesResponder(Context context) {
        super(context);
    }

    @Override
    protected Settings createSettings() {
        this.mockSettings = mock(Settings.class);
        return this.mockSettings;
    }


    @Override
    protected void createUtilities() {
        try {
            this.sharedPreferencesUtility = mock(SharedPreferencesUtility.class);
            this.mockSharedPreferencesUtility = this.sharedPreferencesUtility;


            this.lockStateUtility = mock(LockStateUtility.class);
            this.mocklockStateUtility = this.lockStateUtility;

            this.smsUtility = mock(SMSUtility.class);
            this.mockSmsUtility = this.smsUtility;


            this.callsUtility = mock(CallsUtility.class);
            this.mockCallsUtility = this.callsUtility;

            this.notificationUtility = mock(NotificationUtility.class);
            this.mockNotificationUtility = this.notificationUtility;

            this.locationUtility = mock(LocationUtility.class);
            this.mockLocationUtility = this.locationUtility;

            this.contactsUtility = mock(ContactsUtility.class);
            this.mockContactsUtility = this.contactsUtility;


            this.motionUtility = mock(MotionUtility.class);
            this.mockMotionUtility = this.motionUtility;


            this.sensorsUtility = mock(SensorsUtility.class);
            this.mockSensorsUtility = this.sensorsUtility;


            this.wiFiUtility = mock(WiFiUtility.class);
            this.mockWiFiUtility = this.wiFiUtility;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }}

    class Emulation {

        private final MockedUtilitiesResponder responder;
        Predicate<String> unansweredCallCallback;
        MockedCallSMSLogEntry[] mockedCallSMSLog;
        Location lastRequestedLocation;

        int currentLocation = 0;
        private MockedContactEntry[] contacts;
        private Predicate<Boolean> lockStateChangeCallback;
        private Location[] locations;

        public Emulation(MockedUtilitiesResponder responder) {
            this.responder = responder;
        }

        public void emulateUnansweredCall(String phoneNumber) {
            if (this.unansweredCallCallback != null) {
                this.unansweredCallCallback.apply(phoneNumber);
            }
        }

        public void emulateSMSCallLog(MockedCallSMSLogEntry[] entries) {
            this.mockedCallSMSLog = entries;
        }

        public void emulateContacts(MockedContactEntry[] entries) {
            this.contacts = entries;
        }

        public void emulateLockStateChange(boolean isLocked) {
            this.lockStateChangeCallback.apply(isLocked);
        }

        public void emulateLocations(Location[] entries) {
            this.locations = entries;
        }

        public void startEmulation() throws Exception {

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Emulation.this.unansweredCallCallback = (Predicate<String>) invocation.getArguments()[0];
                    return null;
                }
            }).when(this.responder.callsUtility).listenForUnansweredCalls(any(Predicate.class));

            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Emulation.this.unansweredCallCallback = null;
                    return null;
                }
            }).when(this.responder.callsUtility).stopListeningForCalls();

            doAnswer(new Answer() {
                @Override
                public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    Date date = (Date) invocation.getArguments()[0];
                    String phoneNumber = (String) invocation.getArguments()[1];

                    for (MockedCallSMSLogEntry entry : Emulation.this.mockedCallSMSLog) {
                        if (entry.isCall == true && entry.date.getTime() > date.getTime() && entry.isOutgoing == true && entry.phoneNumber.equals(phoneNumber)) {
                            return true;
                        }
                    }


                    return false;
                }
            }).when(this.responder.callsUtility).wasOutgoingCallAfterDate(any(Date.class), anyString());

            // Contact book

            when(this.responder.contactsUtility.contactBookContainsNumber(anyString())).thenAnswer(new Answer() {

                @Override
                public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    String phoneNumber = (String) invocation.getArguments()[0];
                    for (MockedContactEntry entry : Emulation.this.contacts) {
                        if (entry.phoneNumber.equals(phoneNumber)) {
                            return true;
                        }
                    }
                    return false;
                }

            });


                    when(this.responder.contactsUtility.hasGroupNumberByGroupName(anyString(), anyString())).thenAnswer(new Answer() {

                        @Override
                        public Boolean answer(InvocationOnMock invocation) throws Throwable {
                            String groupID = (String) invocation.getArguments()[0];
                            String phoneNumber = (String) invocation.getArguments()[1];
                            for (MockedContactEntry entry : Emulation.this.contacts) {
                                if (entry.phoneNumber.equals(phoneNumber) && entry.groupID.equals(groupID)) {
                                    return true;
                                }

                            }
                            return false;
                        }
                    });


                            when(this.responder.contactsUtility.isAbleToReadCurrentDeviceNumber()).thenReturn(false);
                            when(this.responder.contactsUtility.readCurrentMobileCountryCode()).thenReturn(260);


                            // Location

                            when(this.responder.locationUtility.getAccurateLocation(anyFloat(), anyFloat(), anyInt())).thenAnswer(new Answer() {

                                public Future<Location> answer(InvocationOnMock invocation) throws Throwable {
                                final Future<Location> value = SettableFuture.create();

                                (new Thread() {
                                    public void run () {
                                       Emulation. this.lastRequestedLocation =     Emulation.this.locations[currentLocation];
                                        value.set(lastRequestedLocation);
                                        currentLocation++;
                                    }
                                }

                                ).

                                        start()

                                return value;

                            });

                            // lock state

                            when(this.responder.lockStateUtility).listenToLockStateChanges(any(Predicate.class)).thenAnswer(new Answer() {
                                @Override
                                public Object answer(InvocationOnMock invocation) throws Throwable {
                                    this.lockStateCallback = (Predicate<String>) invocation.getArguments()[0];
                                    return null;
                                }
                            });


                        }
                    }
        }

            }
        }

        class MockedCallSMSLogEntry {
            public Date date;
            public String phoneNumber;
            public boolean isOutgoing;
            public boolean isCall;
        }

        class MockedContactEntry {
            public String phoneNumber;
            public String name;
            String groupID;
        }

