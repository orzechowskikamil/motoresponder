class CompatibilitySuiteActivity extends Activity {


public void onCreate(){ super.onCreate() this.loadFromResource(R.layout.compatibility_suite);} 

public void startTests(){
  runner = new CompatibilitySuiteRunner(this.getContext());
  boolean result = runner.isDeviceCompatible();
  this.updateUIWithResult(result);
}


private void updateUIWithResult(boolean result){
    
}


}
