@RunWith(AndroidJUnit4.class)
@SmallTest
public class SMSUtilityTest{

    String CURRENT_NUMBER = "791467855";

    @Before
    public void setUp(){
    this.smsUtility = new ExposedSMSUtility();
    if (this.CURRENT_NUMBER==null){ throw new RuntimeException("Current number must be the same as on device to fullfill all tests");}
    }
    
    @Test 
    public void testReadingFromSMSLog(){
    }
    
    @Test
    public void testOfSendingAndListeningForSMS(){
    SMSObject receivedSMS=null;
    boolean smsSentCallbackFired=false; boolean smsReceivedCallbackFired=false;
    int MAX_DURATION_MS = 15000;
    int timeSpentMs = 0;
    
    this.smsUtility.listenForSMS(new Predicate<SMSObject>(){
        public boolean apply(Predicate<SMSObject> sms){
        smsFromListening=sms;
        }
    });
    
    this.smsUtility.sendSMS(PHONE_NUMBER,MESSAGE,new Predicate<SMSObject>(){
        public boolean apply(Predicate<SMSObject> sms){
        smsSentCallbackFired=true;
        }
    });
    
    do{
    Thread.sleep(500);
    timeSpentMs+=500;
    
    }while(!(smsSentCallbackFired && smsReceivedCallbackFired) && timeSpentMs < MAX_DURATION_MS);
    
    assertTrue(smsSentCallbackFired);
    assertTrue(smsReceivedCallbackFired);
    assertTrue (receivedSMS.message.equals(MESSAGE));
    assertTrue (receivedSMS.phoneNumber.equals(PHONE_NUMBER));
    }
    
    @Test
    public void testGetDateOfLastSmsSent(){
      // it's hard to find appropriate data on device, so this test will use mocked data
    }
    
    

}

class ExposedSMSUtility extends SMSUtility{
  MatrixCursor smsLog;
  
  public Cursor query(  whichColumns, selections, selectionArgs, sortOrder){
    if (smsLog == null){
        super(  whichColumns, selections, selectionArgs, sortOrder);
    }else{
    return this.smsLog;
    }
  }
}

class VirtualDatabase extends SQLiteOpenHelper {
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(creationSQL);
    }
    
    public void insertData(){
            SQLiteDatabase db = getWritableDatabase();
            db.insertOrThrow();
    }
}
