 class Reducer implements Store.Reducer<Action, State> {
   
   
        public State reduce(Action action, State currentState) {
            return ImmutableState.builder().from(currentState)
                    .gps(this.reduceGPS(action, currentState.gps()))
                    .accelerometer(this.reduceAccelerometer(action, currentState.accelerometer()))
             respondingProcesses.(this.reduceRespondingProcesses(action, currentState.respondingProcesses()))
             settings.(this.reduceSettings(action, currentState.settings()))
             proximity.(this.reduce(action, currentState.proximity()))
                    .build();
        }
 }



        State.Settings reduceSettings(Action action, State.Settings settings) {
            if (action.type instanceof Actions.Settings) {
                Actions.Settings type = (Actions.Settings) action.type;
                switch (type) {
//                     case SET_RAMPING:
//                         return ImmutableSettings.copyOf(settings).withRamping((Boolean) action.value);
//                     case SET_VIBRATE:
//                         return ImmutableSettings.copyOf(settings).withVibrate((Boolean) action.value);
//                     case SET_SNAP:
//                         return ImmutableSettings.copyOf(settings).withSnap((Boolean) action.value);
//                     case SET_RINGTONE:
//                         return ImmutableSettings.copyOf(settings).withRingtone((String) action.value);
//                     case SET_THEME:
//                         return ImmutableSettings.copyOf(settings).withTheme((Integer) action.value);
                }
            }
            return settings;
        }

State.GPS reduceGPS(Action action, State.GPS settings) {
            if (action.type instanceof Actions.Settings) {
                Actions.Settings type = (Actions.Settings) action.type;
                switch (type) {
                 case ON: return ImmutableGPS.copyOf(settings).withIsEnabled(true);
                 case OFF:return ImmutableGPS.builder()
                           .isEnabled(false)
                            .locationUpdates(ImmutableList.of())
                            .build();
                 case LOCATION_UPDATE: return ImmutableGPS.copyOf(settings)
                     .withLocationUpdates(ImmutableList.of(settings.updatesList,(LocationUpdate) action.value);
                
                
                }
            }
 return settings;
}

                                          
                                          State.Accelerometer reduce(Action action, State.Accelerometer settings) {
            if (action.type instanceof Actions.Settings) {
                Actions.Settings type = (Actions.Settings) action.type;
                switch (type) {
                 case ON: return ImmutableAccelerometer.copyOf(settings).withIsEnabled(true);
                 case OFF:return ImmutableAccelerometer.builder()
                           .isEnabled(false)
                            .eventsList(ImmutableList.of())
                            .build();
                 case MOTION_EVENT: return ImmutableAccelerometer.copyOf(settings)
                     .withLocationUpdates(ImmutableList.of(settings.updatesList,(AccelerometerEvent) action.value);
                }
            }
 return settings;
}
                                          

//// resp process reducer
      State. RespondingProcesses reduceRespondingProcesses(Action action,State. RespondingProcesses old) {
            if (action.type instanceof Actions.RespondingProcess) {
                Actions.RespondingProcess type = (Actions.RespondingProcess) action.type;
                switch (type) {
                  case INCOMING_CALL: 
                     return ImmutableRespondingProcesses.copyOf(old).builder()
                             .list(this.addToList(ImmutableCallRespondingProcess.builder()
                                                .id(old.nextId)
                                                .phoneNumber((String)action.value))
                                                .build())
                             .id(old.nextId+1)
                             .build();
                  case INCOMING_MESSAGE:
                       return ImmutableRespondingProcesses.copyOf(old).builder()
                             .list(this.addToList(ImmutableMessageRespondingProcess.builder()
                                                .id(old.nextId)
                                                .phoneNumber((String)((Pair)action.value).first))
                                                .message((String)((Pair)action.value).second))
                                                .build())
                             .id(old.nextId+1)
                             .build();
                  case RESPOND_WITH_MESSAGE: return;
                }
             return old;
        }
        
        List<RespondingProcess> addToList(RespondingProcess process){
            var copy = new ArrayList<>(old.list);
            copy.add(process);
            return copy;
        }
        
        //// resp process reducer

        State.Proximity reduceProximity(Action action, State.Proximity oldState) {
            if (action.type instanceof Actions.Proximity) {
                Actions.Proximity type = (Actions.Proximity) action.type;
                switch (type) {
                    case PROXIMITY:
                        return ImmutableProximity.copyOf(oldState).withProximity(true);
                    case NO_PROXIMITY:
                        return ImmutableProximity.copyOf(oldState).withProximity(false);
                }
            }
            return oldState;
        }
    }
}
