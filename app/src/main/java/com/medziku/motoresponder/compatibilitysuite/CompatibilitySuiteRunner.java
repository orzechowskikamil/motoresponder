/**
 * Purpose of this is to have suite of simple, quick tests, which we can run on any device, and which
 * can be embedded into a feature of production app, to measure if device is compatible with an application.
 * Those tests require real android device, but doesn't require user interaction, they look if we are able to 
 * read required device APIs
 */


public class CompatibilitySuiteRunner {
    
    List<CompatibilityTestCase> testCases;
    private Context context;
    public CompatibilitySuiteRunner(Context context){this.context=context;this.testCases = this.createTestCases();}
    private List<> createTestCases(){
        list= new ArrayList<>();
        
        list.add(new SMSUtilityCompatibilityTestCase());
    }
    public boolean isDeviceCompatible(){
        
        for (CompatibilityTestCase case : this.testCases){
            result = case.run();
            
            if (!result.isCompatible){
                return false;
            }
        }
        
    }
}
