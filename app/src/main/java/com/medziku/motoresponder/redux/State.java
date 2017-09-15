@Value.Immutable
@Gson.TypeAdapters
public interface State {
    GPS gps();
    Accelerometer accelerometer();
    RespondingProcesses respondingProcesses();
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
    interface RespondingProcesses{
        List<RespondingProcess> list;
        int nextId;
        
          @Value.Immutable
    interface RespondingProcess{
        String phoneNumber;
        int id;
    }
    
    @Value.Immutable
    interface CallRespondingProcess extends RespondingProcess{
    }
    @Value.Immutable
    interface MessageRespondingProcess extends RespondingProcess{
        String message;
    }
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
                    .respondingProcesses(ImmutableRespondingProcesses.builder()
                                         .list(new ArrayList<ImmutableRespondingProcess>())
                                         .nextId(0)
                                         ).build()
                    .settings(ImmutableSettings.builder()
                      .isEnabled(false)
                      .build()
                    ).build();
        }
}
