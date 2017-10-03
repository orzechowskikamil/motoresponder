package com.medziku.motoresponder.redux;

import com.medziku.motoresponder.redux.reducers.RespondingProcessesReducer;
import trikita.jedux.Action;
import trikita.jedux.Store;

class Reducer implements Store.Reducer<Action, State> {

    private Store.Reducer<Action, State> respondingProcessesReducer = new RespondingProcessesReducer();


    public State reduce(Action action, State currentState) {
        State newState = new State();

        newState.gps = this.reduceGPS(action, currentState.gps);
        newState.accelerometer = this.reduceAccelerometer(action, currentState.accelerometer);
        newState.settings = this.reduceSettings(action, currentState.settings);
        newState.proximity = this.reduceProximity(action, currentState.proximity);

        newState = this.respondingProcessesReducer.reduce(action, currentState);

        return newState;
    }


    State.Settings reduceSettings(Action action, State.Settings oldSettings) {
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

    State.GPS reduceGPS(Action action, State.GPS gps) {
        if (action.type instanceof Actions.GPS) {
            Actions.GPS type = (Actions.GPS) action.type;
            State.GPS newGps = gps.clone();

            switch (type) {
                case ON:
                    newGps.isEnabled = true;
                    return newGps;
                case OFF:
                    newGps.isEnabled = false;
                    newGps.updatesList = new ArrayList<>();
                    return newGps;
                case LOCATION_UPDATE:
                    newGps.updatesList = newGps.updatesList.union((State.GPS.LocationUpdate) action.value);
                    return newGps;
            }
        }
        return gps;
    }

    State.Accelerometer reduceAccelerometer(Action action, State.Accelerometer oldAccelerometer) {
        if (action.type instanceof Actions.Accelerometer) {
            Actions.Accelerometer type = (Actions.Accelerometer) action.type;
            State.Accelerometer newAccelerometer = oldAccelerometer.clone();
            switch (type) {
                case ON:
                    newAccelerometer.isEnabled = true;
                    return newAccelerometer;
                case OFF:
                    newAccelerometer.isEnabled = false;
                    newAccelerometer.eventsList = new ArrayList<>();
                    return newAccelerometer;
                case MOTION_EVENT:
                    newAccelerometer.eventsList = newAccelerometer.eventsList.union((State.Accelerometer.Event) action.value);
                    return newAccelerometer;
            }
        }
        return oldAccelerometer;
    }



    // doesnt matter which action

//    }

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

