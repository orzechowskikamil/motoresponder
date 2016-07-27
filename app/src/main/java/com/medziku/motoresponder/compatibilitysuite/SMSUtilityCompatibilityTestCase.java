public class SmsUtilityCompatibilityTestCase extends CompatibilityTestCase{

    private SMSUtility smsUtility;
    
    public SmsUtilityCompatibilityTestCase(Context context){
        super(context);
        this.smsUtility = new SmsUtility(this.context);
    }
    
  public CompatibilityTestResult runTest(){
      this.smsUtility.read
  }
    
}
