
/**
 * It's simple case for compatibility test. 
 * Compatibility tests are intended to run in main app, without testing environment, 
 * so using any test suite like JUnit is not possible.
 */
abstract class CompatibilityTestCase{
  protected Context context;
  public CompatibilityTestCase(Context context) { this.context=context;}

 abstract public CompatibilityTestResult runTest();

}

public CompatibilityTestResult{
    public boolean isCompatible;
}

