/**
 * This class allows to steer mocked utilities, allowing to simulate behavior of device 
 */
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

        public void simulateUnansweredCall(String phoneNumber) {
            if (this.unansweredCallCallback != null) {
                this.unansweredCallCallback.apply(phoneNumber);
            }
        }

        public void setFixedSMSCallLog(MockedCallSMSLogEntry[] entries) {
            this.mockedCallSMSLog = entries;
        }

        public void setFixedContacts(MockedContactEntry[] entries) {
            this.contacts = entries;
        }

        public void simulateLockStateChange(boolean isLocked) {
            this.lockStateChangeCallback.apply(isLocked);
        }

        public void setFixedListOfLocations(Location[] entries) {
            this.locations = entries;
        }
        private void setUpCallsUtility(){
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
        }
        
        private void setUpContactsUtility(){
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

        }
        
        private void setUpLocationUtility(){
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
        }
        
        private void setUpLockStateUtility(){
              when(this.responder.lockStateUtility).listenToLockStateChanges(any(Predicate.class)).thenAnswer(new Answer() {
                                @Override
                                public Object answer(InvocationOnMock invocation) throws Throwable {
                                    this.lockStateCallback = (Predicate<String>) invocation.getArguments()[0];
                                    return null;
                                }
                            });
        }

        public void startEmulation() throws Exception {

            this.setUpCallsUtility();

            // Contact book
            this.setUpContactsUtility();
            

this.setUpLocationUtility()

this.setUpLockStateUtility();


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

