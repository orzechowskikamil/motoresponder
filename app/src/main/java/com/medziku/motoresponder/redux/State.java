package com.medziku.motoresponder.redux;

import com.medziku.motoresponder.redux.state.RespondingProcesses;
import com.medziku.motoresponder.utils.SMSObject;

public class State implements Cloneable {

    public GPS gps = new GPS();
    public Accelerometer accelerometer = new Accelerometer();
    public RespondingProcesses respondingProcesses = new RespondingProcesses();
    public Settings settings = new Settings();
    public Calls calls = new Calls();
    public Messages messages = new Messages();
    public boolean proximity;

    public State clone() {
        try {
            return (State) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    class Calls implements Cloneable {
        public ArrayListFn<String> unhandledCalls=new ArrayListFn<>();

        public Calls clone() {
            try {
                return (Calls) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }
    }

    public class Messages implements Cloneable {
        public ArrayListFn<SMSObject> toSend=new ArrayListFn<>();

        public Messages clone() {
            try {
                return (Messages) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }
    }

    class Accelerometer implements Cloneable {
        public ArrayListFn<Accelerometer.Event> eventsList=new ArrayListFn<>();
        public boolean isEnabled;

        public Accelerometer clone() {
            try {
                return (Accelerometer) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }

        public class Event {
        }

    }


    class Settings implements Cloneable {
        public boolean isEnabled;

        public Settings clone() {
            try {
                return (Settings) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }
    }

    class Contacts implements Cloneable {

        public Contacts clone() {
            try {
                return (Contacts) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }
    }

    class GPS implements Cloneable {
        public ArrayListFn<LocationUpdate> updatesList=new ArrayListFn<>();
        public boolean isEnabled;

        public GPS clone() {
            try {
                return (GPS) super.clone();
            } catch (CloneNotSupportedException e) {
            }
            return null;
        }

        class LocationUpdate {
        }
    }
}


class Default {
    public static State build() {
        return new State();
    }
}
