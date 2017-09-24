package com.medziku.motoresponder.redux;

import android.util.Pair;
import trikita.jedux.Action;
import trikita.jedux.Store;

class Reducer implements Store.Reducer<Action, State> {

    public State reduce(Action action, State currentState) {
        State newState = new State();

        newState.gps = this.reduceGPS(action, currentState.gps);
        newState.accelerometer = this.reduceAccelerometer(action, currentState.accelerometer);
        newState.respondingProcesses = this.reduceRespondingProcesses(action, currentState.respondingProcesses);
        newState.settings = this.reduceSettings(action, currentState.settings);
        newState.proximity = this.reduceProximity(action, currentState.proximity);

        return newState;
    }


    Settings reduceSettings(Action action, Settings oldSettings) {
        if (action.type instanceof Actions.Settings) {
            Actions.Settings type = (Actions.Settings) action.type;
            switch (type) {
                case LOAD_SETTINGS:
                    break;
                case SETTINGS_UPDATED:
                    break;
            }
        }
        return oldSettings;
    }

    GPS reduceGPS(Action action, GPS gps) {
        if (action.type instanceof Actions.GPS) {
            Actions.GPS type = (Actions.GPS) action.type;

            GPS newGps = gps.clone();

            switch (type) {
                case ON:
                    newGps.isEnabled = true;
                    return newGps;
                case OFF:
                    newGps.isEnabled = false;
                    newGps.updatesList = newGps.getClearUpdatesList();
                    return newGps;
                case LOCATION_UPDATE:
                    newGps.updatesList = newGps.getUpdatesListWith((LocationUpdate) action.value);
                    return newGps;
            }
        }
        return gps;
    }


    Accelerometer reduceAccelerometer(Action action, Accelerometer oldAccelerometer) {
        if (action.type instanceof Actions.Accelerometer) {
            Actions.Accelerometer type = (Actions.Accelerometer) action.type;
            Accelerometer newAccelerometer = oldAccelerometer.clone();
            switch (type) {
                case ON:
                    newAccelerometer.isEnabled = true;
                    return newAccelerometer;
                case OFF:
                    newAccelerometer.isEnabled = false;
                    newAccelerometer.eventsList = newAccelerometer.getClearEventsList();
                    return newAccelerometer;
                case MOTION_EVENT:
                    newAccelerometer.eventsList = newAccelerometer.getEventsListWith((AccelerometerEvent) action.value);
                    return newAccelerometer;
            }
        }
        return oldAccelerometer;
    }


    RespondingProcesses reduceRespondingProcesses(Action action, RespondingProcesses old) {
        if (action.type instanceof Actions.RespondingProcess) {
            Actions.RespondingProcess type = (Actions.RespondingProcess) action.type;

            RespondingProcesses newRespondingProcesses = old.clone();

            switch (type) {
                case INCOMING_CALL:
                    newRespondingProcesses.nextId++;
                    CallRespondingProcess newProcess = new CallRespondingProcess((String) action.value,
                            newRespondingProcesses.nextId);
                    newRespondingProcesses.list = newRespondingProcesses.getListWith(newProcess);
                    return newRespondingProcesses;
                case INCOMING_MESSAGE:
                    newRespondingProcesses.nextId++;
                    MessageRespondingProcess newMsgProcess = new MessageRespondingProcess(
                            (String) ((Pair) action.value).first,
                            (String) ((Pair) action.value).second,
                            newRespondingProcesses.nextId);

                    newRespondingProcesses.list = newRespondingProcesses.getListWith(newMsgProcess);
                    return newRespondingProcesses;
//                case RESPOND_WITH_MESSAGE:
//                    return ;
            }
        }
        return old;
    }


    boolean reduceProximity(Action action, boolean oldState) {
        if (action.type instanceof Actions.Proximity) {
            Actions.Proximity type = (Actions.Proximity) action.type;
            switch (type) {
                case PROXIMITY:
                    return true;
                case NO_PROXIMITY:
                    return false;
            }
        }
        return oldState;
    }

}

