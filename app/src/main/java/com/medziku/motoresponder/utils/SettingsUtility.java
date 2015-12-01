class SettingsUtility{

    private SharedPreferences.Editor editor;
    public SettingsUtility(Context context){
      SharedPreferences sharedPref = context.getActivity().getPreferences(Context.MODE_PRIVATE);
      SharedPreferences.Editor this.editor = sharedPref.edit();
      editor.putInt(getString(R.string.saved_high_score), newHighScore);
      editor.commit();
    }
    
    private boolean getValue(String name, boolean defaultValue){
        return this.getBoolean(name,defaultValue);
    }
    
    private void setValue(String name, boolean value){
      this.editor.putBoolean(name, value);
      this.editor.commit();
    }
    
        private String getValue(String name, String defaultValue){
        return this.getString(name,defaultValue);
    }
    
    private void setValue(String name, String value){
      this.editor.putString(name, value);
      this.editor.commit();
    }
    
        private int getValue(String name, int defaultValue){
        return this.getInt(name,defaultValue);
    }
    
    private void setValue(String name, int value){
      this.editor.putInt(name, value);
      this.editor.commit();
    }
    
    private String SERVICE_ENABLED="service-enabled";
    
    public boolean isServiceEnabled(){
        return this.getValue(SERVICE_ENABLED, true);
    }
    
    public void setServiceEnabled(boolean value){
      this.setValue(SERVICE_ENABLED, value);
    }
    
    public String getAutoResponseTextForSMS()(){
        return "(Automatyczna odpowiedz) Czesc, jezdze wlasnie motocyklem, odezwe sie jak skonczy mi sie paliwo.";
    }
    
}
