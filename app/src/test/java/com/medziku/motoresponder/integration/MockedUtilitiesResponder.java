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
