
/**
 * It's simple case for compatibility test. 
 * Compatibility tests are intended to run in main app, without testing environment, 
 * so using any test suite like JUnit is not possible.
 */
abstract class CompatibilityTestCase{
  protected Context context;
  public CompatibilityTestCase(Context context) { this.context=context;}
  abstract public String getName();

 abstract protected void runTest();
 public CompatibilityTestResult run(){
   CompatibilityTestResult result = new CompatibilityTestResult();
   result.name=this.getName();
   try{
     this.runTest();
   }catch(Exception e){
     result.isCompatible = false;
     result.errorMessage = e.getMessage();
   }
   result.isCompatible=true;

}

public CompatibilityTestResult{
    public boolean isCompatible;
    public String name;
    public String errorMessage;
}

