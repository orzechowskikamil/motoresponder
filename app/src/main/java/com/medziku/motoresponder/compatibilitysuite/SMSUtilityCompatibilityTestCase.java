public class SmsUtilityCompatibilityTestCase extends CompatibilityTestCase{

    private SMSUtility smsUtility;
    
    public SmsUtilityCompatibilityTestCase(Context context){
        super(context);
        this.smsUtility = new SmsUtility(this.context);
    }
    
  public void runTest() {
      this.checkIfAbleToReadAnythingFromSMSLog();
  }
  
  private void checkIfAbleToReadAnythingFromSMSLog(){
  Cursor cursor = this.smsUtility.querySmsLog(['id','date','etc'], null,null,null);

if (cursor.length() == 0){
    throw new RuntimeException("Looks like SMSes can't be read");
}
  }
}
