@Value.Immutable
@Gson.TypeAdapters
public interface State {
    GPS gps();
    Accelerometer accelerometer();
    List<RespondingProcess> respondingProcesses();
    Settings settings();
    Proximity proximity();
    
    
       @Value.Immutable
    interface Proximity{
    boolean proximity;
    }
    
    @Value.Immutable
    interface GPS{}
    
    @Value.Immutable
    interface Accelerometer{}
    
    @Value.Immutable
    interface RespondingProcess{
        String phoneNumber;
    }
    
    @Value.Immutable
    interface CallRespondingProcess extends RespondingProcess{
    }
    
    interface MessageRespondingProcess extends RespondingProcess{
        String message;
    }
    
    @Value.Immutable
    interface Settings{
    boolean isEnabled();
    }
    
    @Value.Immutable
    interface Contacts{
    }
    
    
}
    
     class Default {
        public static State build() {
            return ImmutableState.builder()
                    .gps(ImmutableGPS.builder().build())
                    .accelerometer(ImmutableAccelerometer.builder().build())
                    .respondingProcesses(new ArrayList<ImmutableRespondingProcess>())
                    .settings(ImmutableSettings.builder()
                      .isEnabled(false)
                      .build()
                    ).build();
        }
}
