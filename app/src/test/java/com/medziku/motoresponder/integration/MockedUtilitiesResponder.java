class MockedUtilitiesResponder extends Responder {

    public Settings mockSettings;
    public SharedPreferencesUtility mockSharedPreferencesUtility;
    public MockedLockStateUtility mockedLockStateUtility;
    public MockedSMSUtility mockedSmsUtility;
    public NotificationUtility mockNotificationUtility;
    public MockedCallsUtility mockedCallsUtility;
    public MockedLocationUtility mockedLocationUtility;
    public MockedContactsUtility mockedContactsUtility;
    public SensorsUtility mockSensorsUtility;
    public MockedMotionUtility mockMotionUtility;
    public WiFiUtility mockWiFiUtility;

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
                
            // FOLLOW THIS IDEA!!!!!    
                
            this.mockedSMSUtility = new MockedSMSUtility();
            this.smsUtility =this.mockSMSUtility.mock;
            
            this.mockedCallsUtility = new MockedCallsUtility();
            this.callsUtility = this.mockedCallsUtility.mock;
            

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
