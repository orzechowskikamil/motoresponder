@Value.Immutable
@Gson.TypeAdapters
public interface State {
    GPS gps();
    Accelerometer accelerometer();
    List<RespondingProcess> respondingProcesses();
    Settings settings();
    
    
    @Value.Immutable
    interface GPS{}
    
    @Value.Immutable
    interface Accelerometer{}
    
    @Value.Immutable
    interface RespondingProcess{}
    
    @Value.Immutable
    interface Settings{
    boolean isEnabled();
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
                    
   
   class Reducer implements Store.Reducer<Action, State> {
   
   
        public State reduce(Action action, State currentState) {
            return ImmutableState.builder().from(currentState)
                    .alarm(reduceAlarm(action, currentState.alarm()))
                    .settings(reduceSettings(action, currentState.settings()))
                    .build();
        }

        State.Settings reduceSettings(Action action, State.Settings settings) {
            if (action.type instanceof Actions.Settings) {
                Actions.Settings type = (Actions.Settings) action.type;
                switch (type) {
                    case SET_RAMPING:
                        return ImmutableSettings.copyOf(settings).withRamping((Boolean) action.value);
                    case SET_VIBRATE:
                        return ImmutableSettings.copyOf(settings).withVibrate((Boolean) action.value);
                    case SET_SNAP:
                        return ImmutableSettings.copyOf(settings).withSnap((Boolean) action.value);
                    case SET_RINGTONE:
                        return ImmutableSettings.copyOf(settings).withRingtone((String) action.value);
                    case SET_THEME:
                        return ImmutableSettings.copyOf(settings).withTheme((Integer) action.value);
                }
            }
            return settings;
        }

        State.Alarm reduceAlarm(Action action, State.Alarm alarm) {
            if (action.type instanceof Actions.Alarm) {
                Actions.Alarm type = (Actions.Alarm) action.type;
                switch (type) {
                    case ON:
                        return ImmutableAlarm.copyOf(alarm).withOn(true);
                    case OFF:
                        return ImmutableAlarm.copyOf(alarm).withOn(false);
                    case SET_MINUTE:
                        return ImmutableAlarm.copyOf(alarm).withMinutes((Integer) action.value);
                    case SET_HOUR:
                        return ImmutableAlarm.copyOf(alarm).withHours((Integer) action.value);
                    case SET_AM_PM:
                        return ImmutableAlarm.copyOf(alarm).withAm((Boolean) action.value);
                }
            }
            return alarm;
        }
    }
}
