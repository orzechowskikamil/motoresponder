class Proximity{

Proximity(Store store,Context context){
this.store=store;
this.proximityUtility=new ProximityUtility(context)
}

start(){ this.proximityUtility.listenToProximity(

);

}

  
  /*
  
   private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;

    private Sensor proximitySensor;

    private float currentProximity;
    private boolean isListening;
 
    public SensorsUtility(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.proximitySensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    public void registerSensors() {
        if (this.isListening == true) {
            return;
        }

        this.isListening = true;

        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    .this.setCurrentProximity(event.values[0]);
                }

            }
        };


        if (this.proximitySensor != null) {
            this.sensorManager.registerListener(
                    this.sensorEventListener,
                    this.proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }

    }

    private void setCurrentProximity(float currentProximity) {
        this.currentProximity = currentProximity;
       boolean isProxime = this.currentProximity != this.proximitySensor.getMaximumRange();
       dispatchAction()
    }




    public void unregisterSensors() {
        if (this.isListening == false) {
            return;
        }
       
        this.sensorManager.unregisterListener(this.sensorEventListener);
         this.isListening = false;
    }


*/
